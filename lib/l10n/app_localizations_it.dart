// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for Italian (`it`).
class AppLocalizationsIt extends AppLocalizations {
  AppLocalizationsIt([String locale = 'it']) : super(locale);

  @override
  String get ok => 'Ok';

  @override
  String get cancel => 'Annulla';

  @override
  String get mainActionAbout => 'Info';

  @override
  String get mainActionTryAgain => 'Riprova';

  @override
  String get aboutTitle => 'Info';

  @override
  String get aboutAuthorTitle => 'Autore';

  @override
  String get aboutRepositoryTitle => 'Guarda il codice sorgente!';

  @override
  String get aboutRepositorySubtitle => 'Github';

  @override
  String get aboutVersion => 'Versione';

  @override
  String get outageMaintenance => 'Lavori programmati';

  @override
  String get outageBlackout => 'Guasto';

  @override
  String get outageCause => 'Causa';

  @override
  String get outageExpectedRestore => 'Previsione ripristino';

  @override
  String get outageLastUpdate => 'Ultimo aggiornamento';

  @override
  String get outageOfflineCustomers => 'Clienti disalimentati';

  @override
  String get outageStartOutage => 'Inizio interruzioni';

  @override
  String get outageShowDetails => 'Mostra dettagli';

  @override
  String get outageHideDetails => 'Nascondi dettagli';

  @override
  String get outageFetchFailure => 'Si è verificato un errore nel recupero delle interruzioni';

  @override
  String get notificationChannelCreationFailure => 'Impossibile creare il canale di notifica';

  @override
  String get notificationPermissionDeniedTitle => 'Autorizzazione negata';

  @override
  String get notificationPermissionDeniedText => 'Consenti all\'app di inviarti notifiche per utilizzare questa funzionalità';

  @override
  String get settingsTrackOutagesTitle => 'Aggiornamenti sui guasti';

  @override
  String get settingsTrackOutagesSubtitle => 'Abilita un tasto nel dettaglio del guasto per tracciarne lo stato';

  @override
  String get settingsTrackOutagesDialogTitle => 'Aggiornamenti sui guasti';

  @override
  String get settingsTrackOutagesDialogMessage => 'Dopo aver abilitato il tracciamento dal dettaglio del guasto riceverai una notifica con data e ora stimati del suo ripristino ogni 15 minuti.\n\nAttenzione! A causa delle limitazioni imposte dal sistema operativo potrebbe non funzionare correttamente.';

  @override
  String trackOutageNotificationMessage(String expectedRestore) {
    return 'Previsione ripristino: $expectedRestore';
  }
}
