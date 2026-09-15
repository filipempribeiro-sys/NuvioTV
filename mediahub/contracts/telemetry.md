# MEDIA•HUB telemetry policy

Core functionality must not depend on telemetry. If diagnostics/analytics are later enabled, collect only coarse operational events needed to improve reliability and make them transparent/configurable where appropriate.

Never collect playback URLs containing credentials, IPTV credentials, account tokens, QR/device secrets, full search histories as identity profiles, or contents of private addon configuration. Crash reports must redact request headers and sensitive configuration.

A telemetry outage must have zero effect on playback, navigation, account access or sync.
