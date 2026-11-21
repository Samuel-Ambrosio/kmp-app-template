# Instructions for Claude Code

Multi-module Kotlin Multiplatform template (Compose Multiplatform, Clean Architecture, Koin, Ktor, Navigation 3) targeting Android, iOS, desktop (JVM) and web (Wasm + JS). The root `README.md` is the source of truth for the stack, the module structure, what runs on which platform and the steps to create a new app from the template: do not duplicate it here; if it gets out of date, fix it there.

## Language

- **Claude documentation and configuration are always written in English**: this file, `.claude/rules/`, `.claude/agents/`, `.claude/skills/`, hook scripts and their messages, and any other file under `.claude/`. This applies even when the conversation happens in another language.
- **Reply to the user in the language they write in.** The user may write in any language; answer in that same language.

## Contextual rules

The rules in `.claude/rules/` load automatically based on the file being touched (`paths:` frontmatter):

`kotlin-multiplatform`, `domain-layer`, `data-layer`, `viewmodel`, `feature-modules`, `compose-views`, `navigation`, `koin-modules`, `analytics`, `sdk-integrations`, `logging`, `build-system`, `ci-cd`.

`engineering-principles.md` and `proactive-critique.md` have no `paths:`: they load always, in every session.

## Agents

| Agent | When to use it |
| --- | --- |
| `reviewer` | Validate the quality of a change before commit/PR against `.claude/rules/`. Only reports, does not fix |

## Hooks

- `debug-log-guard.sh` (`PreToolUse` on `Edit|Write`): blocks `println(`, `android.util.Log.*`, `NSLog(` and `console.log(` in Kotlin files (see `rules/logging.md`). If it blocks something that should be allowed, review the hook; do not work around it.

## Verification before considering a change done

The CI checks (`.github/workflows/ci.yml`), runnable locally on macOS:

```bash
./gradlew detekt jvmTest testAndroidHostTest jsNodeTest wasmJsNodeTest verifyRoborazziAndroidHostTest verifyRoborazziJvm :androidApp:assembleDebug :desktopApp:compileKotlin :webApp:wasmJsBrowserDistribution --continue
```

Add the checks of what the change touches:

- `iosMain`, iOS interop or `iosApp/`: `./gradlew iosSimulatorArm64Test verifyRoborazziIosSimulatorArm64` and build `iosApp` (`xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' build`).
- Compose UI in a module with web targets: `jsBrowserTest wasmJsBrowserTest` need Chrome. CI has it; locally, if Chrome is not installed, say that they were not run instead of skipping them silently.
- An intended UI change: re-record the screenshots of the three platforms (`recordRoborazzi*`) in the same change, after looking at the new images.

A source set only compiles when one of its targets is built: a green JVM/Android build says nothing about `iosMain` or `webMain`. A green build does not prove the app starts either: if the change touches startup (`startApp`, the hosts), the Koin graph, `expect`/`actual`, R8 or the manifest, launch the affected app (emulator, simulator, desktop window, browser; see `engineering-principles.md`).

## Autonomy and decisions

**Decision making**: when an approach fails or unexpected complexity shows up, present the options to the user and wait for their decision. Do not discard alternatives, revert changes or take the "easy way out" unilaterally. "It can't be done" is followed by "how do you want to proceed?", never by a direct action.

**Anthropic server errors**: if an agent or subagent fails because of an API error (529 overloaded, 500, rate limit), do not retry automatically: tell the user and wait for instructions.

## Subagents — restricted use

Launch an exploratory search subagent only when the search really spans many unknown files or separate areas of the codebase; for a targeted search, use `grep`/`find` directly. The `reviewer` agent is invoked by task type, not by number of files.

## Git

- Main branch: `main` (CI runs on it).
- Commit messages in English, one imperative sentence that reads on its own (`Add Ktor client to the remote data source`).
- Changes to `.claude/` go in a separate commit from code changes, so they can be identified and reverted on their own.
