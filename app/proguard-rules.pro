# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve useful release stack traces for Crashlytics/R8 mapping.
-keepattributes SourceFile,LineNumberTable

# Keep Room database metadata generated from annotations.
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Keep public app model constructors stable for Firebase/Room reflective paths.
-keepclassmembers class com.findyourpet.app.data.local.entity.** {
    public <init>(...);
}

# Firestore constructs this profile DTO reflectively through toObject().
# Keep the class identity, empty constructor, readable accessors, and backing
# fields used to map existing users/{uid} documents in minified releases.
-keep class com.findyourpet.app.data.profile.UserProfileDocument {
    public <init>();
    public java.lang.String getUid();
    public java.lang.String getDisplayName();
    public java.lang.String getEmail();
    public long getCreatedAt();
    public long getUpdatedAt();
}
-keepclassmembers class com.findyourpet.app.data.profile.UserProfileDocument {
    private java.lang.String uid;
    private java.lang.String displayName;
    private java.lang.String email;
    private long createdAt;
    private long updatedAt;
}

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
