#import "DocumentScanner.h"

#import <React/RCTBridgeModule.h>
#import <React/RCTUtils.h>

@interface RNDocumentScannerVision : NSObject
+ (instancetype)shared;
- (void)scan:(NSDictionary *)options
     resolve:(RCTPromiseResolveBlock)resolve
      reject:(RCTPromiseRejectBlock)reject;
@end

@implementation DocumentScanner

// ✅ Must match the codegen signature from NativeDocumentScannerSpec
- (void)scan:(JS::NativeDocumentScanner::DocumentScannerOptions &)options
     resolve:(RCTPromiseResolveBlock)resolve
      reject:(RCTPromiseRejectBlock)reject
{
  NSMutableDictionary *dict = [NSMutableDictionary new];

  // std::optional fields (only set if provided)
  if (options.allowGallery().has_value()) {
    dict[@"allowGallery"] = @(options.allowGallery().value());
  }
  if (options.pageLimit().has_value()) {
    dict[@"pageLimit"] = @(options.pageLimit().value());
  }
  if (options.returnPdf().has_value()) {
    dict[@"returnPdf"] = @(options.returnPdf().value());
  }
  if (options.returnJpeg().has_value()) {
    dict[@"returnJpeg"] = @(options.returnJpeg().value());
  }

  [[RNDocumentScannerVision shared] scan:dict resolve:resolve reject:reject];
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
  return std::make_shared<facebook::react::NativeDocumentScannerSpecJSI>(params);
}

+ (NSString *)moduleName
{
  return @"DocumentScanner";
}

@end
