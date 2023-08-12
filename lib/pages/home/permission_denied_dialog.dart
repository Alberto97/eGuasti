import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class PermissionDeniedDialog extends StatelessWidget {
  const PermissionDeniedDialog({
    Key? key,
    required this.onOkPressed,
  }) : super(key: key);

  final VoidCallback onOkPressed;

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(AppLocalizations.of(context).notificationPermissionDeniedTitle),
      content: Text(AppLocalizations.of(context).notificationPermissionDeniedText),
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
