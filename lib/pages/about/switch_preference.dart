import 'package:flutter/material.dart';

class SwitchPreference extends StatelessWidget {
  const SwitchPreference({
    Key? key,
    required this.value,
    required this.onChanged,
    required this.title,
    required this.subtitle,
    this.leading,
  }) : super(key: key);

  final Widget? leading;

  final String title;
  final String subtitle;

  final bool value;
  final ValueChanged<bool> onChanged;

  void toggleValue() => onChanged(!value);

  @override
  Widget build(BuildContext context) {
    return ListTile(
      minVerticalPadding: 16.0,
      leading: leading,
      title: Text(title),
      subtitle: Text(subtitle),
      trailing: Switch(
        value: value,
        onChanged: (_) => toggleValue(),
      ),
      onTap: () => toggleValue(),
    );
  }
}
