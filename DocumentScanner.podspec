require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "DocumentScanner"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  # ✅ VisionKit requires iOS 13+
  s.platforms    = { :ios => "13.0" }

  s.source       = {
    :git => "https://github.com/FernandoAOborges/react-native-document-scanner.git",
    :tag => "#{s.version}"
  }

  # ✅ Include Swift + ObjC++ sources
  s.source_files = "ios/**/*.{h,m,mm,swift,cpp}"
  s.private_header_files = "ios/**/*.h"

  # ✅ Link VisionKit (Apple Document Scanner UI)
  s.frameworks = "VisionKit"

  # ✅ Ensure Swift is enabled for the Pod
  s.swift_version = "5.0"

  install_modules_dependencies(s)
end
