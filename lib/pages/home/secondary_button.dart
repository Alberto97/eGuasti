import 'package:flutter/material.dart';

class SecondaryButton extends StatelessWidget {
  /// Called when the button is tapped or otherwise activated.
  ///
  /// If this callback and [onLongPress] are null, then the button will be disabled.
  ///
  /// See also:
  ///
  ///  * [enabled], which is true if the button is enabled.
  final VoidCallback? onPressed;

  /// Typically the button's label.
  final Widget? child;

  const SecondaryButton({Key? key, this.onPressed, this.child})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Center(
      child: SizedBox(
        width: 350,
        child: ElevatedButton(
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.transparent,
            foregroundColor: Theme.of(context).primaryColor,
            side: BorderSide(
              color: Colors.grey.withOpacity(0.5),
            ),
          ).copyWith(
            elevation: MaterialStateProperty.all(0.0),
          ),
          onPressed: onPressed,
          child: child,
        ),
      ),
    );
  }
}
