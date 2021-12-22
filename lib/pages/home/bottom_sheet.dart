import 'package:flutter/material.dart';
import 'package:flutter_e_guasti/models/outage.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class HomeBottomSheet extends StatefulWidget {
  const HomeBottomSheet({
    Key? key,
    required this.place,
    required this.cause,
    required this.start,
    required this.expectedRestore,
    required this.offlineCustomers,
    required this.lastUpdate,
  }) : super(key: key);

  final String place;
  final Cause cause;
  final String start;
  final String expectedRestore;
  final int offlineCustomers;
  final String lastUpdate;

  @override
  State<HomeBottomSheet> createState() => _HomeBottomSheetState();
}

class _HomeBottomSheetState extends State<HomeBottomSheet> {
  @override
  Widget build(BuildContext context) {
    return Wrap(
      children: [
        Padding(
          padding: const EdgeInsets.all(20.0),
          child: Text(
            widget.place,
            style: Theme.of(context).textTheme.headline6,
          ),
        ),
        buildDetails(
          Icons.offline_bolt_rounded,
          AppLocalizations.of(context)!.outageCause,
          buildCauseText(),
        ),
        buildDetails(
          Icons.error_outline_rounded,
          AppLocalizations.of(context)!.outageStartOutage,
          widget.start,
        ),
        buildDetails(
          Icons.restore_rounded,
          AppLocalizations.of(context)!.outageExpectedRestore,
          widget.expectedRestore,
        ),
        buildDetails(
          Icons.supervised_user_circle_rounded,
          AppLocalizations.of(context)!.outageOfflineCustomers,
          widget.offlineCustomers.toString(),
        ),
        buildDetails(
          Icons.access_time_rounded,
          AppLocalizations.of(context)!.outageLastUpdate,
          widget.lastUpdate,
        ),
        SizedBox(
          height: 8.0,
          child: Container(),
        )
      ],
    );
  }

  String buildCauseText() {
    return widget.cause == Cause.failure
        ? AppLocalizations.of(context)!.outageBlackout
        : AppLocalizations.of(context)!.outageMaintenance;
  }

  Widget buildDetails(IconData icon, String title, String summary) {
    return Padding(
      padding: const EdgeInsets.symmetric(
        horizontal: 16.0,
        vertical: 8.0,
      ),
      child: Row(
        children: [
          Padding(
            padding: const EdgeInsets.only(right: 8.0),
            child: Icon(icon, color: Colors.grey[700]),
          ),
          Text(title, style: Theme.of(context).textTheme.bodyText2),
          const Spacer(),
          Text(summary, style: Theme.of(context).textTheme.bodyText1)
        ],
      ),
    );
  }
}
