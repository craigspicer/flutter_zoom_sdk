import 'dart:core';
import 'dart:io';
import 'dart:convert';

void main(List<String> args) async {
  var location = Platform.script.toString();
  var isNewFlutter = location.contains(".snapshot");
  if (isNewFlutter) {
    var sp = Platform.script.toFilePath();
    var sd = sp.split(Platform.pathSeparator);
    sd.removeLast();
    var scriptDir = sd.join(Platform.pathSeparator);
    var packageConfigPath = [scriptDir, '..', '..', '..', 'package_config.json']
        .join(Platform.pathSeparator);
    var jsonString = File(packageConfigPath).readAsStringSync();
    Map<String, dynamic> packages = jsonDecode(jsonString);
    var packageList = packages["packages"];
    String? zoomFileUri;
    for (var package in packageList) {
      if (package["name"] == "flutter_zoom_sdk") {
        zoomFileUri = package["rootUri"];
        break;
      }
    }
    if (zoomFileUri == null) {
      print("flutter_zoom_sdk package not found!");
      return;
    }
    location = zoomFileUri;
  }
  if (Platform.isWindows) {
    location = location.replaceFirst("file:///", "");
  } else {
    location = location.replaceFirst("file://", "");
  }
  if (!isNewFlutter) {
    location = location.replaceFirst("/bin/unzip_zoom_sdk.dart", "");
  }

  await checkAndDownloadSDK(location);

  print('Complete');
}

Future<void> checkAndDownloadSDK(String location) async {
  var iosSDKFile = location +
      '/ios/MobileRTC.xcframework/ios-arm64/MobileRTC.framework/MobileRTC';
  bool exists = await File(iosSDKFile).exists();

  if (!exists) {
    await downloadFile(
        Uri.parse('https://www.dropbox.com/scl/fi/6c80i4z961hjloow3gsy0/MobileRTC?rlkey=urt3jexxia2n7ia5mmlr95ad2&st=st3hzqmk&dl=1'),
        iosSDKFile);
  }

  var iosSimulateSDKFile = location +
      '/ios/MobileRTC.xcframework/ios-arm64_x86_64-simulator/MobileRTC.framework/MobileRTC';
  exists = await File(iosSimulateSDKFile).exists();

  if (!exists) {
    await downloadFile(
        Uri.parse('https://www.dropbox.com/scl/fi/pix9lacdfen8msyj08dk0/MobileRTC?rlkey=wqjrvnelkynw9ryz255z094ua&st=pqkncadl&dl=1'),
        iosSimulateSDKFile);
  }

  var androidRTCLibFile = location + '/android/libs/mobilertc.aar';
  exists = await File(androidRTCLibFile).exists();
  if (!exists) {
    await downloadFile(
        Uri.parse(
            'https://www.dropbox.com/scl/fi/5innd2hwmxu95v4yaycy0/mobilertc.aar?rlkey=4iw9hbg6icjbcon6xopeuig3b&st=px8bfsdh&dl=1'),
        androidRTCLibFile);
  }
}

Future<void> downloadFile(Uri uri, String savePath) async {
  print('Download ${uri.toString()} to $savePath');
  File destinationFile = await File(savePath).create(recursive: true);

  final request = await HttpClient().getUrl(uri);
  final response = await request.close();
  await response.pipe(destinationFile.openWrite());
}
