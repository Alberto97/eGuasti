import 'package:eguasti/models/outage.dart';
import 'package:eguasti/pages/home/secondary_button.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:flutter/material.dart';

class OutageSheet extends StatefulWidget {
  final bool tracking;
  final Outage outage;
  final VoidCallback track;

  const OutageSheet({
    Key? key,
    required this.outage,
    required this.track,
    required this.tracking,
  }) : super(key: key);

  @override
  State<OutageSheet> createState() => _OutageSheetState();
}

class _OutageSheetState extends State<OutageSheet> {
  bool showDetails = false;

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Padding(
        padding: const EdgeInsets.only(right: 16.0, left: 16.0, bottom: 25.0),
        child: Stack(alignment: Alignment.topRight, children: [
          Material(
            borderRadius: BorderRadius.circular(8),
            elevation: 8.0,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Padding(
                  padding: const EdgeInsets.only(top: 20.0, left: 20.0),
                  child: Text(
                    widget.outage.place,
                    style: Theme.of(context).textTheme.headline6,
                  ),
                ),
                buildType(),
                buildItem(
                  Icons.restore_rounded,
                  AppLocalizations.of(context)!.outageExpectedRestore,
                  widget.outage.expectedRestore,
                ),
                AnimatedSwitcher(
                  duration: const Duration(milliseconds: 250),
                  child: showDetails
                      ? buildItems()
                      : const SizedBox.shrink(),
                ),
                buildDetailsButton(),
                const SizedBox(
                  height: 16.0,
                )
              ],
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: buildTrackButton(),
          ),
        ]),
      ),
    );
  }

  Widget buildTrackButton() {
    return FloatingActionButton(
      onPressed: () => widget.track(),
      child: AnimatedSwitcher(
        duration: const Duration(milliseconds: 500),
        child: widget.tracking
            ? const Icon(Icons.notifications_off, key: ValueKey("off"))
            : const Icon(Icons.notifications, key: ValueKey("on")),
      ),
      backgroundColor: Theme.of(context).primaryColor,
      elevation: 0.0,
    );
  }

  Widget buildDetailsButton() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20.0),
      child: SecondaryButton(
        onPressed: () {
          setState(() {
            showDetails = !showDetails;
          });
        },
        child: Text(
          showDetails
              ? AppLocalizations.of(context)!.outageHideDetails
              : AppLocalizations.of(context)!.outageShowDetails,
        ),
      ),
    );
  }

  Widget buildType() {
    final title = AppLocalizations.of(context)!.outageCause;
    var reason = AppLocalizations.of(context)!.outageMaintenance;
    var icon = Icons.build_rounded;
    if (widget.outage.cause == Cause.failure) {
      reason = AppLocalizations.of(context)!.outageBlackout;
      icon = Icons.bolt_rounded;
    }
    return buildItem(icon, title, reason);
  }

  Widget buildItems() {
    return Column(
      children: [
        buildItem(
          Icons.error_outline_rounded,
          AppLocalizations.of(context)!.outageStartOutage,
          widget.outage.start,
        ),
        buildItem(
          Icons.supervised_user_circle_rounded,
          AppLocalizations.of(context)!.outageOfflineCustomers,
          widget.outage.offlineCustomers.toString(),
        ),
        buildItem(
          Icons.access_time_rounded,
          AppLocalizations.of(context)!.outageLastUpdate,
          widget.outage.lastUpdate,
        ),
      ],
    );
  }

  Widget buildItem(IconData icon, String title, String summary) {
    return ListTile(
      leading: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Padding(
            padding: const EdgeInsets.only(left: 5),
            child: Icon(
              icon,
              color: Colors.grey[600],
              size: 30,
            ),
          ),
        ],
      ),
      title: Text(title),
      subtitle: Text(summary),
    );
  }
}
