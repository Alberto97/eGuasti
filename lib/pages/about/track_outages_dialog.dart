import 'package:eguasti/l10n/app_localizations.dart';
import 'package:flutter/material.dart';

class TrackOutagesDialog extends StatelessWidget {
  const TrackOutagesDialog({
    super.key,
    required this.onOkPressed,
  });

  final VoidCallback onOkPressed;

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(AppLocalizations.of(context).settingsTrackOutagesDialogTitle),
      content: Text(AppLocalizations.of(context).settingsTrackOutagesDialogMessage),
      actions: [
        TextButton(
          child: Text(AppLocalizations.of(context).cancel),
          onPressed: () => Navigator.of(context).pop(),
        ),
        TextButton(
          child: Text(AppLocalizations.of(context).ok),
          onPressed: () {
            onOkPressed();
            Navigator.of(context).pop();
          },
        ),
      ],
    );
  }
}
