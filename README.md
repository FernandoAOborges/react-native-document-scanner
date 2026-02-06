# @fernandoaoborges/react-native-document-scanner

Document Scanner for **React Native (Android)** powered by **Google ML Kit Document Scanner**.

This library uses the official native document scanning flow provided by Google Play Services, including automatic document detection, cropping, perspective correction, and export.

> ⚠️ **Status**
>
> - ✅ Android supported  
> - ❌ iOS not implemented yet

---

## 📦 Installation

### 1) Install the package
```bash
yarn add @fernandoaoborges/react-native-document-scanner
```

or

```bash
npm install @fernandoaoborges/react-native-document-scanner
```

---

## 🤖 Android Requirements

This module depends on **Google Play Services ML Kit – Document Scanner**.

- **Minimum SDK:** 24+
- Device must have **Google Play Services** installed

No additional configuration is required in `AndroidManifest.xml`.

---

## 🔄 Rebuild the app

After installation, rebuild the Android app:

```bash
cd android
./gradlew clean
```

Then run:

```bash
yarn android
```

---

## 🚀 Usage (Android)

```ts
import { scanDocument } from '@fernandoaoborges/react-native-document-scanner';

const result = await scanDocument({
  allowGallery: true,
  pageLimit: 3,
  returnJpeg: true,
  returnPdf: true,
});

console.log(result);
```

---

## ⚙️ Options

| Option | Type | Default | Description |
|------|------|---------|-------------|
| `allowGallery` | boolean | `true` | Allow importing images from gallery |
| `pageLimit` | number | `10` | Maximum number of pages |
| `returnJpeg` | boolean | `true` | Return scanned pages as JPEG |
| `returnPdf` | boolean | `true` | Return scanned document as PDF |

---

## 📄 Result

```ts
{
  images: Array<{
    uri: string;
    width: number;
    height: number;
    fileSize: number;
  }>;
  pdf?: {
    uri: string;
    pageCount: number;
  };
}
```

---

## ❗ Important Notes

- The camera and preview flow is **fully native** (Google ML Kit)
- No JavaScript-based UI is used
- The scanner opens as a **native Google Activity**
- Ideal for document apps, onboarding, KYC, contracts, and forms

---

## 📜 License

MIT © Fernando A. Borges
