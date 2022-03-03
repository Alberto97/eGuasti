import 'package:eguasti/pages/about/about_bloc.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:flutter_svg/flutter_svg.dart';

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
          buildUrl(),
          const Divider(),
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

  Widget buildUrl() {
    return ListTile(
      leading: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          SvgPicture.asset('assets/github.svg', width: 30.0, height: 30.0),
        ],
      ),
      title: Text(AppLocalizations.of(context)!.aboutRepositoryTitle),
      subtitle: Text(AppLocalizations.of(context)!.aboutRepositorySubtitle),
      onTap: () => bloc.openRepository(),
    );
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
