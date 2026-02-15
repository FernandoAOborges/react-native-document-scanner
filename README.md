# @fernandoaoborges/react-native-document-scanner

Native **Document Scanner** for **React Native** using official platform
scanning flows:

-   **Android:** Google Play Services ML Kit Document Scanner\
-   **iOS:** Apple VisionKit (`VNDocumentCameraViewController`)

Automatic document detection, edge cropping, perspective correction,
multi-page scanning and export to **JPEG** and **PDF** --- with **zero
JavaScript camera UI**.

------------------------------------------------------------------------

## ✅ Status

-   ✅ Android supported (ML Kit)
-   ✅ iOS supported (VisionKit)

------------------------------------------------------------------------

## 📦 Installation

``` bash
yarn add @fernandoaoborges/react-native-document-scanner
```

or

``` bash
npm install @fernandoaoborges/react-native-document-scanner
```

------------------------------------------------------------------------

## 🔄 Rebuild the app

### iOS

``` bash
cd ios
pod install
cd ..
yarn ios
```

> ⚠️ VisionKit works best on real devices. It may not be available on
> Simulator.

### Android

``` bash
cd android
./gradlew clean
cd ..
yarn android
```

------------------------------------------------------------------------

## 🤖 Android Requirements

-   Minimum SDK: 24+
-   Google Play Services installed on device

No additional `AndroidManifest.xml` configuration required.

------------------------------------------------------------------------

## 🍎 iOS Requirements

-   Minimum iOS: 13+
-   Add this key to your `Info.plist`:

``` xml
<key>NSCameraUsageDescription</key>
<string>We need camera access to scan documents.</string>
```

------------------------------------------------------------------------

## 🚀 Usage

``` ts
import { scanDocument } from '@fernandoaoborges/react-native-document-scanner';

const result = await scanDocument({
  allowGallery: true,
  pageLimit: 3,
  returnJpeg: true,
  returnPdf: true,
});

console.log(result);
```

------------------------------------------------------------------------

## ⚙️ Options

  Option         Type      Default   Notes
  -------------- --------- --------- ----------------------------------
  allowGallery   boolean   true      Android only. Ignored on iOS.
  pageLimit      number    \-        Maximum number of pages returned
  returnJpeg     boolean   true      Return scanned pages as JPEG
  returnPdf      boolean   false     Return scanned document as PDF

------------------------------------------------------------------------

## 📄 Result

``` ts
export type DocumentScannerResult = {
  canceled: boolean;
  images: string[]; // file URIs or paths
  pdf?: string;     // file URI or path
};
```

------------------------------------------------------------------------

## ❗ Important Notes

-   The scanner UI is fully native
-   No JavaScript camera implementation
-   Android opens official Google scanner
-   iOS opens official Apple VisionKit scanner
-   Ideal for KYC, onboarding, contracts and forms

------------------------------------------------------------------------

## 📜 License

MIT © Fernando A.O. Borges
