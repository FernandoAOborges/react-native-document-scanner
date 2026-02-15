# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a
Changelog](https://keepachangelog.com/en/1.0.0/) and this project
follows [Semantic Versioning](https://semver.org/).

------------------------------------------------------------------------

## \[0.2.0\] - 2026-02-14

### 🚀 Added

-   iOS support using Apple VisionKit (`VNDocumentCameraViewController`)
-   Multi-page scanning on iOS
-   JPEG export on iOS
-   PDF export on iOS
-   `pageLimit` support on iOS
-   Unified cross-platform API (`scan(options)`)

### 🛠 Improved

-   Consistent return structure across platforms:
    -   `canceled: boolean`
    -   `images: string[]`
    -   `pdf?: string`
-   iOS now returns `file://` URIs for exported files

### 🔒 Stability

-   Proper TurboModule signature alignment with codegen
-   Safe Promise resolution handling (avoiding EXC_BAD_ACCESS)
-   Improved native thread handling for VisionKit presentation

------------------------------------------------------------------------

## \[0.1.0\] - 2026-02-01

### 🎉 Initial Release

### 🚀 Added

-   Android support using Google ML Kit Document Scanner (Play Services)
-   Native Google scanning Activity integration
-   Automatic edge detection and perspective correction
-   JPEG export support
-   PDF export support
-   `allowGallery` option (Android)
-   `pageLimit` option (Android)
-   TurboModule implementation (New Architecture)

------------------------------------------------------------------------

## Future

### 🔜 Planned

-   Cache cleanup API (`clearCache()`)
-   Optional JPEG quality configuration
-   Cross-platform output normalization improvements
-   Metadata return (width, height, fileSize)
-   Optional OCR integration (separate package)

------------------------------------------------------------------------

Maintained by Fernando A.O. Borges
