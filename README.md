# CircularProgressIndicator
Displays the progress in a circle with animation along with the progress percentage.

##Demo

![](https://github.com/vishnus1224/CircularProgressIndicator/blob/master/Project/demo/demo.gif)


#Sample Usage

Add following line to the build.gradle file in order to use the library.
```
compile 'com.vishnus1224.circularprogressindicator:library:1.0.1'
```

Fully customizable in xml with the following attributes:

```
<declare-styleable name="CircularProgressIndicator">
        <attr name="progressColor" format="color|reference"/>
        <attr name="backgroundColor" format="color|reference"/>
        <attr name="strokeWidth" format="integer"/>
        <attr name="progress" format="integer"/>
        <attr name="maxProgress" format="integer"/>
        <attr name="textSize" format="dimension"/>
        <attr name="textColor" format="color|reference"/>
 </declare-styleable>
```

For using these, check the example project or sample code below.

Give a name to the namespace. In the code below, it is named "custom".

`xmlns:custom="http://schemas.android.com/apk/res-auto"`

View declaration in layout xml file: Specify the view width and height as per requirements.
```
<com.vishnus1224.circularprogressindicator.CircularProgressIndicator
        custom:strokeWidth="50"
        custom:progress="40"
        custom:progressColor="#ffAF7D49"
        custom:backgroundColor="#ff444444"
        custom:textColor="#ff343957"
        custom:textSize="50sp"
        custom:maxProgress="100"
        android:layout_width="300dp"
        android:layout_height="300dp" />
```

To animate progress to a value, call the `animateProgress(long duration, int finalValue)` function on the view with the animation duration in milliseconds and the final progress value.

e.g. To set the progress to 80 with an animation duration of half a second, call 
```animateProgress(500, 80)```.
