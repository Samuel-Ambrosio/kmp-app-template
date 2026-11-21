#!/bin/bash
# .claude/hooks/debug-log-guard.sh
# PreToolUse hook: blocks println(), android.util.Log.*, NSLog() and
# console.log() in Kotlin files. Ref: .claude/rules/logging.md
#
# Use AppLogger instead. The logging backend (Kermit writers) is excluded by
# path so maintaining it doesn't trip this guard.

INPUT=$(cat)

FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // .tool_input.path // ""')

# Only check Kotlin files
if [[ "$FILE_PATH" != *.kt ]]; then
    exit 0
fi

# Logging backend -- not a precedent to extend
case "$FILE_PATH" in
    *KermitLogger.kt|*CrashlyticsLogWriter.kt)
        exit 0 ;;
esac

CONTENT=$(echo "$INPUT" | jq -r '.tool_input.content // .tool_input.new_string // ""')

block() {
    jq -n --arg reason "$1" '{continue: false, stopReason: $reason}'
    exit 0
}

if echo "$CONTENT" | grep -qE '(^|[^A-Za-z0-9_.])println[[:space:]]*\('; then
    block "❌ println() is forbidden in Kotlin.

Use AppLogger: AppLogger.d(...), .i(...), .w(...), .e(...).
println() bypasses the logging backend and the startup buffer.
See .claude/rules/logging.md"
fi

if echo "$CONTENT" | grep -qE '(^|[^A-Za-z0-9_.])Log\.(v|d|i|w|e|wtf)[[:space:]]*\('; then
    block "❌ android.util.Log.* is forbidden in Kotlin outside the logging backend.

Use AppLogger: AppLogger.d(...), .i(...), .w(...), .e(...).
See .claude/rules/logging.md"
fi

if echo "$CONTENT" | grep -qE '(^|[^A-Za-z0-9_.])NSLog[[:space:]]*\('; then
    block "❌ NSLog() is forbidden in Kotlin outside the logging backend.

Use AppLogger: AppLogger.d(...), .i(...), .w(...), .e(...).
See .claude/rules/logging.md"
fi

if echo "$CONTENT" | grep -qE '(^|[^A-Za-z0-9_])console\.(log|info|warn|error|debug)[[:space:]]*\('; then
    block "❌ console.log() and friends are forbidden in Kotlin outside the logging backend.

Use AppLogger: AppLogger.d(...), .i(...), .w(...), .e(...).
See .claude/rules/logging.md"
fi

exit 0
