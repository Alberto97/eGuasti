import 'package:eguasti/l10n/app_localizations.dart';
import 'package:eguasti/pages/about/about_page.dart';
import 'package:eguasti/pages/home/home_screen.dart';
import 'package:eguasti/tools/background_tasks.dart';
import 'package:eguasti/ui/colors.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:workmanager/workmanager.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Hive.initFlutter();
  Workmanager().initialize(
    callbackDispatcher,
    isInDebugMode: kDebugMode,
  );
  runApp(const MyApp());
}

@pragma('vm:entry-point')
Future<void> callbackDispatcher() async {
  await Hive.initFlutter();
  Workmanager()
      .executeTask((task, inputData) => backgroundTasks[task]!(inputData));
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'eGuasti',
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      theme: ThemeData(
        primarySwatch: AppColors.darkBlue,
        useMaterial3: false
      ),
      initialRoute: '/',
      routes: {
        '/': (BuildContext context) => const HomePage(),
        '/about': (context) => const AboutPage(),
      },
    );
  }
}
