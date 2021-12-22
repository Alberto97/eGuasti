import 'package:flutter/material.dart';
import 'package:flutter_e_guasti/pages/about/about_bloc.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class AboutPage extends StatefulWidget {
  const AboutPage({Key? key}) : super(key: key);

  @override
  State<AboutPage> createState() => _AboutPageState();
}

class _AboutPageState extends State<AboutPage> {
  late AboutBloc bloc;

  @override
  void initState() {
    super.initState();
    bloc = AboutBloc();
  }

  @override
  void dispose() {
    bloc.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: buildTitle(),
      ),
      body: ListView(
        children: [
          buildAppVersion(),
          buildAuthor(),
        ],
      ),
    );
  }

  Widget buildTitle() {
    var title = AppLocalizations.of(context)!.aboutTitle;
    return Text(title);
  }

  Widget buildAppVersion() {
    return ListTile(
      leading: const Text(""),
      title: Text(AppLocalizations.of(context)!.aboutVersion),
      subtitle: StreamBuilder<String>(
        initialData: "Unknown",
        stream: bloc.appVersion,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            return Text(snapshot.data!);
          } else {
            return const Text("Unknown");
          }
        },
      ),
    );
  }

  Widget buildAuthor() {
    return ListTile(
      leading: const Text(""),
      title: Text(AppLocalizations.of(context)!.aboutAuthorTitle),
      subtitle: const Text("Alberto Pedron"),
    );
  }
}
