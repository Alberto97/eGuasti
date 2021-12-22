import 'package:flutter/material.dart';
import 'package:flutter_e_guasti/pages/about/about_page.dart';
import 'package:flutter_e_guasti/pages/home/home_screen.dart';
import 'package:flutter_e_guasti/ui/colors.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      theme: ThemeData(
        primarySwatch: AppColors.darkBlue,
      ),
      initialRoute: '/',
      routes: {
        '/': (BuildContext context) => const HomePage(),
        '/about': (context) => const AboutPage(),
      },
    );
  }
}
