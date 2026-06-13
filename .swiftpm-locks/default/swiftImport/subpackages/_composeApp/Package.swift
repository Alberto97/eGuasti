// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_composeApp",
  platforms: [
    .iOS("15.0")
  ],
  products: [
    .library(
      name: "_composeApp",
      type: .none,
      targets: ["_composeApp"]
    )
  ],
  dependencies: [
    .package(
      url: "https://github.com/maplibre/maplibre-gl-native-distribution.git",
      from: "6.25.1"
    )
  ],
  targets: [
    .target(
      name: "_composeApp",
      dependencies: [
        .product(
          name: "MapLibre",
          package: "maplibre-gl-native-distribution"
        )
      ]
    )
  ]
)
