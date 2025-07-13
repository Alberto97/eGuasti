import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:intl/intl.dart' as intl;

import 'app_localizations_en.dart';
import 'app_localizations_it.dart';

// ignore_for_file: type=lint

/// Callers can lookup localized strings with an instance of AppLocalizations
/// returned by `AppLocalizations.of(context)`.
///
/// Applications need to include `AppLocalizations.delegate()` in their app's
/// `localizationDelegates` list, and the locales they support in the app's
/// `supportedLocales` list. For example:
///
/// ```dart
/// import 'l10n/app_localizations.dart';
///
/// return MaterialApp(
///   localizationsDelegates: AppLocalizations.localizationsDelegates,
///   supportedLocales: AppLocalizations.supportedLocales,
///   home: MyApplicationHome(),
/// );
/// ```
///
/// ## Update pubspec.yaml
///
/// Please make sure to update your pubspec.yaml to include the following
/// packages:
///
/// ```yaml
/// dependencies:
///   # Internationalization support.
///   flutter_localizations:
///     sdk: flutter
///   intl: any # Use the pinned version from flutter_localizations
///
///   # Rest of dependencies
/// ```
///
/// ## iOS Applications
///
/// iOS applications define key application metadata, including supported
/// locales, in an Info.plist file that is built into the application bundle.
/// To configure the locales supported by your app, you’ll need to edit this
/// file.
///
/// First, open your project’s ios/Runner.xcworkspace Xcode workspace file.
/// Then, in the Project Navigator, open the Info.plist file under the Runner
/// project’s Runner folder.
///
/// Next, select the Information Property List item, select Add Item from the
/// Editor menu, then select Localizations from the pop-up menu.
///
/// Select and expand the newly-created Localizations item then, for each
/// locale your application supports, add a new item and select the locale
/// you wish to add from the pop-up menu in the Value field. This list should
/// be consistent with the languages listed in the AppLocalizations.supportedLocales
/// property.
abstract class AppLocalizations {
  AppLocalizations(String locale)
      : localeName = intl.Intl.canonicalizedLocale(locale.toString());

  final String localeName;

  static AppLocalizations of(BuildContext context) {
    return Localizations.of<AppLocalizations>(context, AppLocalizations)!;
  }

  static const LocalizationsDelegate<AppLocalizations> delegate =
      _AppLocalizationsDelegate();

  /// A list of this localizations delegate along with the default localizations
  /// delegates.
  ///
  /// Returns a list of localizations delegates containing this delegate along with
  /// GlobalMaterialLocalizations.delegate, GlobalCupertinoLocalizations.delegate,
  /// and GlobalWidgetsLocalizations.delegate.
  ///
  /// Additional delegates can be added by appending to this list in
  /// MaterialApp. This list does not have to be used at all if a custom list
  /// of delegates is preferred or required.
  static const List<LocalizationsDelegate<dynamic>> localizationsDelegates =
      <LocalizationsDelegate<dynamic>>[
    delegate,
    GlobalMaterialLocalizations.delegate,
    GlobalCupertinoLocalizations.delegate,
    GlobalWidgetsLocalizations.delegate,
  ];

  /// A list of this localizations delegate's supported locales.
  static const List<Locale> supportedLocales = <Locale>[
    Locale('en'),
    Locale('it')
  ];

  /// No description provided for @ok.
  ///
  /// In en, this message translates to:
  /// **'Ok'**
  String get ok;

  /// No description provided for @cancel.
  ///
  /// In en, this message translates to:
  /// **'Cancel'**
  String get cancel;

  /// No description provided for @mainActionAbout.
  ///
  /// In en, this message translates to:
  /// **'About'**
  String get mainActionAbout;

  /// No description provided for @mainActionTryAgain.
  ///
  /// In en, this message translates to:
  /// **'Try Again'**
  String get mainActionTryAgain;

  /// No description provided for @aboutTitle.
  ///
  /// In en, this message translates to:
  /// **'About'**
  String get aboutTitle;

  /// No description provided for @aboutAuthorTitle.
  ///
  /// In en, this message translates to:
  /// **'Author'**
  String get aboutAuthorTitle;

  /// No description provided for @aboutRepositoryTitle.
  ///
  /// In en, this message translates to:
  /// **'Check out the source code!'**
  String get aboutRepositoryTitle;

  /// No description provided for @aboutRepositorySubtitle.
  ///
  /// In en, this message translates to:
  /// **'Github'**
  String get aboutRepositorySubtitle;

  /// No description provided for @aboutVersion.
  ///
  /// In en, this message translates to:
  /// **'Version'**
  String get aboutVersion;

  /// No description provided for @outageMaintenance.
  ///
  /// In en, this message translates to:
  /// **'Scheduled maintenance'**
  String get outageMaintenance;

  /// No description provided for @outageBlackout.
  ///
  /// In en, this message translates to:
  /// **'Blackout'**
  String get outageBlackout;

  /// No description provided for @outageCause.
  ///
  /// In en, this message translates to:
  /// **'Cause'**
  String get outageCause;

  /// No description provided for @outageExpectedRestore.
  ///
  /// In en, this message translates to:
  /// **'Expected restore'**
  String get outageExpectedRestore;

  /// No description provided for @outageLastUpdate.
  ///
  /// In en, this message translates to:
  /// **'Last update'**
  String get outageLastUpdate;

  /// No description provided for @outageOfflineCustomers.
  ///
  /// In en, this message translates to:
  /// **'Offline customers'**
  String get outageOfflineCustomers;

  /// No description provided for @outageStartOutage.
  ///
  /// In en, this message translates to:
  /// **'Start outage'**
  String get outageStartOutage;

  /// No description provided for @outageShowDetails.
  ///
  /// In en, this message translates to:
  /// **'Show details'**
  String get outageShowDetails;

  /// No description provided for @outageHideDetails.
  ///
  /// In en, this message translates to:
  /// **'Hide details'**
  String get outageHideDetails;

  /// No description provided for @outageFetchFailure.
  ///
  /// In en, this message translates to:
  /// **'An error occurred while retrieving outages'**
  String get outageFetchFailure;

  /// No description provided for @notificationChannelCreationFailure.
  ///
  /// In en, this message translates to:
  /// **'Failed to create the notification channel'**
  String get notificationChannelCreationFailure;

  /// No description provided for @notificationPermissionDeniedTitle.
  ///
  /// In en, this message translates to:
  /// **'Permission denied'**
  String get notificationPermissionDeniedTitle;

  /// No description provided for @notificationPermissionDeniedText.
  ///
  /// In en, this message translates to:
  /// **'Allow the app to send you notifications in order to use ths feature'**
  String get notificationPermissionDeniedText;

  /// No description provided for @settingsTrackOutagesTitle.
  ///
  /// In en, this message translates to:
  /// **'Outage updates'**
  String get settingsTrackOutagesTitle;

  /// No description provided for @settingsTrackOutagesSubtitle.
  ///
  /// In en, this message translates to:
  /// **'Enables a button in the outage\'s details to track its status'**
  String get settingsTrackOutagesSubtitle;

  /// No description provided for @settingsTrackOutagesDialogTitle.
  ///
  /// In en, this message translates to:
  /// **'Outage updates'**
  String get settingsTrackOutagesDialogTitle;

  /// No description provided for @settingsTrackOutagesDialogMessage.
  ///
  /// In en, this message translates to:
  /// **'Once enabled the tracking from the outage\'s details you\'ll receive a notification with time and date of the estimated restore every 15 minutes.\n\nWarning! Due to limitations of the operating system this feature might not work properly.'**
  String get settingsTrackOutagesDialogMessage;

  /// No description provided for @trackOutageNotificationMessage.
  ///
  /// In en, this message translates to:
  /// **'Expected restore: {expectedRestore}'**
  String trackOutageNotificationMessage(String expectedRestore);
}

class _AppLocalizationsDelegate
    extends LocalizationsDelegate<AppLocalizations> {
  const _AppLocalizationsDelegate();

  @override
  Future<AppLocalizations> load(Locale locale) {
    return SynchronousFuture<AppLocalizations>(lookupAppLocalizations(locale));
  }

  @override
  bool isSupported(Locale locale) =>
      <String>['en', 'it'].contains(locale.languageCode);

  @override
  bool shouldReload(_AppLocalizationsDelegate old) => false;
}

AppLocalizations lookupAppLocalizations(Locale locale) {
  // Lookup logic when only language code is specified.
  switch (locale.languageCode) {
    case 'en':
      return AppLocalizationsEn();
    case 'it':
      return AppLocalizationsIt();
  }

  throw FlutterError(
      'AppLocalizations.delegate failed to load unsupported locale "$locale". This is likely '
      'an issue with the localizations generation tool. Please file an issue '
      'on GitHub with a reproducible sample app and the gen-l10n configuration '
      'that was used.');
}
