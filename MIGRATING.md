# Migration guide

## Migrating to 2.0.0

Version **2.0.0** makes `SelectResponse` the single canonical response model. The
rendering entry points now accept the snake_case offers selection response directly,
and the legacy camelCase wire-response model tree (`NetworkExperienceResponse` and its
subtree) has been removed. Rendering, events, catalog and payment behaviour are
unchanged — only the accepted input shape and the pre-parsed overload's type change.

### Raw responses must be the snake_case selection response

`RoktLayout(experienceResponse: String, …)` and `RoktLayoutView.loadLayout(experienceResponse: String, …)`
now expect the snake_case offers selection response (for example `session_id`,
`session_token`, `page_context`, `plugins[].plugin.config`, `outer_layout_schema`).
CamelCase responses from earlier versions are no longer accepted. Direct and
server-to-server integrations should pass the response through unchanged rather than
re-serialising it.

### Pre-parsed overloads now take `SelectResponse`

The pre-parsed overloads change parameter type from `NetworkExperienceResponse` to the
helper-owned `SelectResponse`:

```kotlin
// Before
RoktLayout(experienceResponse: NetworkExperienceResponse, …)
RoktLayoutView.loadLayout(experienceResponse: NetworkExperienceResponse, …)

// After
RoktLayout(experienceResponse: SelectResponse, …)
RoktLayoutView.loadLayout(experienceResponse: SelectResponse, …)
```

`SelectResponse` is `@Serializable`, so it can be used directly as a Retrofit return
type and cached without a parallel model tree.

### Removed types

`NetworkExperienceResponse` and its wire-response subtree (`NetworkPlugin`,
`NetworkSlotLayout`, `NetworkOfferLayout`, `NetworkCreativeLayout`, `NetworkCatalogItem`,
`NetworkCatalogItemGroup`, `NetworkTransactionData`, `NetworkResponseOption`,
`NetworkAction`, `NetworkSignalType`, `NetworkPageContext`, `NetworkOptions`, …) have
been removed. Decode the snake_case `SelectResponse` instead.

## Migrating to the LayoutFailure reason API

`RoktUxEvent.LayoutFailure` now includes `sessionId` and a `reason` so partners can tell
**no offer returned** apart from **rendering / integration failures**.

Existing checks for `event is RoktUxEvent.LayoutFailure` continue to work. Switch on
`reason` when you need the split:

```kotlin
when (val event = uxEvent) {
    is RoktUxEvent.LayoutFailure -> when (event.reason) {
        RoktUxEvent.LayoutFailure.Reason.NoOffers -> {
            // Offers request succeeded but nothing to show — not an integration bug.
            // Share event.sessionId with your account manager.
        }
        RoktUxEvent.LayoutFailure.Reason.InvalidResponse -> {
            // Experience response could not be decoded or mapped.
        }
        RoktUxEvent.LayoutFailure.Reason.InvalidSchema -> {
            // Layout schema failed validation/transform or a runtime render failure.
        }
        RoktUxEvent.LayoutFailure.Reason.MissingEmbeddedTarget -> {
            // Host app location does not match the embedded placement target.
        }
        RoktUxEvent.LayoutFailure.Reason.PresentationFailed -> {
            // Unused on Android Compose; present for cross-platform parity.
        }
    }
    else -> Unit
}
```

| Reason                  | Meaning                                                     |
| ----------------------- | ----------------------------------------------------------- |
| `NoOffers`              | Response decoded but contained no renderable offers/layouts |
| `InvalidResponse`       | Response could not be decoded or mapped                     |
| `InvalidSchema`         | Layout schema validation/transform or runtime render failed |
| `MissingEmbeddedTarget` | Host location does not match the embedded placement target  |
| `PresentationFailed`    | Unused on Android; API parity with iOS                      |

Console logging (when enabled via `RoktUx.setLogLevel`) now appends
`| sessionId=<id>` once the session is known, and uses distinct messages for `NoOffers`
versus rendering failures.

## Migrating to 2.0.0

Version **2.0.0** makes `SelectResponse` the single canonical response model. The
rendering entry points now accept the snake_case offers selection response directly,
and the legacy camelCase wire-response model tree (`NetworkExperienceResponse` and its
subtree) has been removed. Rendering, events, catalog and payment behaviour are
unchanged — only the accepted input shape and the pre-parsed overload's type change.

### Raw responses must be the snake_case selection response

`RoktLayout(experienceResponse: String, …)` and `RoktLayoutView.loadLayout(experienceResponse: String, …)`
now expect the snake_case offers selection response (for example `session_id`,
`session_token`, `page_context`, `plugins[].plugin.config`, `outer_layout_schema`).
CamelCase responses from earlier versions are no longer accepted. Direct and
server-to-server integrations should pass the response through unchanged rather than
re-serialising it.

### Pre-parsed overloads now take `SelectResponse`

The pre-parsed overloads change parameter type from `NetworkExperienceResponse` to the
helper-owned `SelectResponse`:

```kotlin
// Before
RoktLayout(experienceResponse: NetworkExperienceResponse, …)
RoktLayoutView.loadLayout(experienceResponse: NetworkExperienceResponse, …)

// After
RoktLayout(experienceResponse: SelectResponse, …)
RoktLayoutView.loadLayout(experienceResponse: SelectResponse, …)
```

`SelectResponse` is `@Serializable`, so it can be used directly as a Retrofit return
type and cached without a parallel model tree.

### Removed types

`NetworkExperienceResponse` and its wire-response subtree (`NetworkPlugin`,
`NetworkSlotLayout`, `NetworkOfferLayout`, `NetworkCreativeLayout`, `NetworkCatalogItem`,
`NetworkCatalogItemGroup`, `NetworkTransactionData`, `NetworkResponseOption`,
`NetworkAction`, `NetworkSignalType`, `NetworkPageContext`, `NetworkOptions`, …) have
been removed. Decode the snake_case `SelectResponse` instead.
