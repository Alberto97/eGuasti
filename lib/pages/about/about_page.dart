import 'package:eguasti/pages/about/about_cubit.dart';
import 'package:eguasti/pages/about/switch_preference.dart';
import 'package:eguasti/pages/about/track_outages_dialog.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:flutter_svg/flutter_svg.dart';

class AboutPage extends StatelessWidget {
  const AboutPage({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) => AboutCubit(),
      child: const _AboutPage(),
    );
  }
}

class _AboutPage extends StatelessWidget {
  const _AboutPage();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: buildTitle(context),
      ),
      body: ListView(
        children: [
          buildUrl(context),
          // const Divider(),
          // buildTrackOutageToggle(),
          const Divider(),
          buildAppVersion(context),
          buildAuthor(context),
        ],
      ),
    );
  }

  Widget buildTitle(BuildContext context) {
    var title = AppLocalizations.of(context).aboutTitle;
    return Text(title);
  }

  Widget buildTrackOutageToggle() {
    return BlocBuilder<AboutCubit, AboutState>(
      buildWhen: (previous, current) =>
          previous.trackOutagesEnabled != current.trackOutagesEnabled,
      builder: (context, state) {
        return SwitchPreference(
          leading: const Text(""),
          value: state.trackOutagesEnabled,
          onChanged: (value) => onTrackOutagesChanged(context, value),
          title: AppLocalizations.of(context).settingsTrackOutagesTitle,
          subtitle: AppLocalizations.of(context).settingsTrackOutagesSubtitle,
        );
      },
    );
  }

  void onTrackOutagesChanged(BuildContext context, bool value) {
    final cubit = context.read<AboutCubit>();
    if (value) {
      showDialog(
        context: context,
        builder: (BuildContext context) {
          return TrackOutagesDialog(
            onOkPressed: () => cubit.setTrackOutagesEnabled(value),
          );
        },
      );
    } else {
      cubit.setTrackOutagesEnabled(value);
    }
  }

  Widget buildUrl(BuildContext context) {
    return ListTile(
      leading: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          SvgPicture.asset('assets/github.svg', width: 30.0, height: 30.0),
        ],
      ),
      title: Text(AppLocalizations.of(context).aboutRepositoryTitle),
      subtitle: Text(AppLocalizations.of(context).aboutRepositorySubtitle),
      onTap: () => context.read<AboutCubit>().openRepository(),
    );
  }

  Widget buildAppVersion(BuildContext context) {
    return ListTile(
      leading: const Text(""),
      title: Text(AppLocalizations.of(context).aboutVersion),
      subtitle: BlocBuilder<AboutCubit, AboutState>(
        buildWhen: (previous, current) =>
            previous.appVersion != current.appVersion,
        builder: (context, state) {
          return Text(state.appVersion);
        },
      ),
    );
  }

  Widget buildAuthor(BuildContext context) {
    return ListTile(
      leading: const Text(""),
      title: Text(AppLocalizations.of(context).aboutAuthorTitle),
      subtitle: const Text("Alberto Pedron"),
    );
  }
}
