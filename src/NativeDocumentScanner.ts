import { TurboModuleRegistry, type TurboModule } from 'react-native';

export type DocumentScannerOptions = {
  allowGallery?: boolean;
  pageLimit?: number;
  returnPdf?: boolean;
  returnJpeg?: boolean;
};

export type DocumentScannerResult = {
  canceled: boolean;
  images: string[];
  pdf?: string;
};

export interface Spec extends TurboModule {
  scan(options?: DocumentScannerOptions): Promise<DocumentScannerResult>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('DocumentScanner');
