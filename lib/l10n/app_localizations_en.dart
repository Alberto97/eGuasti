// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for English (`en`).
class AppLocalizationsEn extends AppLocalizations {
  AppLocalizationsEn([String locale = 'en']) : super(locale);

  @override
  String get ok => 'Ok';

  @override
  String get cancel => 'Cancel';

  @override
  String get mainActionAbout => 'About';

  @override
  String get mainActionTryAgain => 'Try Again';

  @override
  String get aboutTitle => 'About';

  @override
  String get aboutAuthorTitle => 'Author';

  @override
  String get aboutRepositoryTitle => 'Check out the source code!';

  @override
  String get aboutRepositorySubtitle => 'Github';

  @override
  String get aboutVersion => 'Version';

  @override
  String get outageMaintenance => 'Scheduled maintenance';

  @override
  String get outageBlackout => 'Blackout';

  @override
  String get outageCause => 'Cause';

  @override
  String get outageExpectedRestore => 'Expected restore';

  @override
  String get outageLastUpdate => 'Last update';

  @override
  String get outageOfflineCustomers => 'Offline customers';

  @override
  String get outageStartOutage => 'Start outage';

  @override
  String get outageShowDetails => 'Show details';

  @override
  String get outageHideDetails => 'Hide details';

  @override
  String get outageFetchFailure => 'An error occurred while retrieving outages';

  @override
  String get notificationChannelCreationFailure => 'Failed to create the notification channel';

  @override
  String get notificationPermissionDeniedTitle => 'Permission denied';

  @override
  String get notificationPermissionDeniedText => 'Allow the app to send you notifications in order to use ths feature';

  @override
  String get settingsTrackOutagesTitle => 'Outage updates';

  @override
  String get settingsTrackOutagesSubtitle => 'Enables a button in the outage\'s details to track its status';

  @override
  String get settingsTrackOutagesDialogTitle => 'Outage updates';

  @override
  String get settingsTrackOutagesDialogMessage => 'Once enabled the tracking from the outage\'s details you\'ll receive a notification with time and date of the estimated restore every 15 minutes.\n\nWarning! Due to limitations of the operating system this feature might not work properly.';

  @override
  String trackOutageNotificationMessage(String expectedRestore) {
    return 'Expected restore: $expectedRestore';
  }
}
