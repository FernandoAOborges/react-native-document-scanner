import NativeDocumentScanner, {
  type DocumentScannerOptions,
  type DocumentScannerResult,
} from './NativeDocumentScanner';

export type { DocumentScannerOptions, DocumentScannerResult };

export function scanDocument(
  options: DocumentScannerOptions = {}
): Promise<DocumentScannerResult> {
  return NativeDocumentScanner.scan(options);
}
