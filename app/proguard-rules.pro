# Keep model classes used by Gson reflection-based parsing
-keep class com.letsfootball.app.data.model.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn okhttp3.**
-dontwarn retrofit2.**
