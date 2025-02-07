# Release steps

```mermaid
---
title: UX Helper releases
---

gitGraph
   commit
   commit tag: "4.7.0"
   branch release/4.7.x
   checkout release/4.7.x
   commit tag: "4.7.1"
   checkout main
   merge release/4.7.x
   commit
   commit
   checkout release/4.7.x
   commit tag: "4.7.2"
   checkout main
   merge release/4.7.x
   commit
   commit tag: "4.8.0"
   commit
   commit tag: "4.9.0-SNAPSHOT"
```

## Pre-release

- For pre-release testing or verifying functionality of the latest version -SNAPSHOT builds can be used
- Every commit on main:
    - Finds the last tag e.g. 4.7.0 bumps the minor version e.g. 4.8.0
    - Generates a build for Maven Central in the format 4.8.0-SNAPSHOT
    - Tags the latest commit on main with the snapshot version e.g. 4.8.0-SNAPSHOT

## Full release

1. Run the workflow called "Create draft release" which will:
    - Open a PR targeting main branch
2. Once tested and approved by the relevant owners, merge the PR to main
3. Once merged the following will occur:
    - Update changelog - unreleased section moved to correct version number
    - Build uploaded to Maven Central
    - Release made on Github with relevant build files
    - Commit tagged with version number

## Hotfix / patch release

1. Find and create a working branch from the tagged commit you need to patch either:
    - On the main branch for the first patch commit
    - On a floating release branch with name in the format "release/4.7.x" for subsequent patches
2. On your working branch make the required changes
3. Create a pull request that targets the relevant release branch with name in the format "release/4.7.x"
4. Approve and merge the pull request.
5. (TODO) Once merged the following will occur:
    - Build uploaded to Maven Central
    - Release made on Github with relevant build files
    - Commit tagged with version number
