package com.flatcode.littlemovie.Unit;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DATA {
    public static String USERS = "Users";
    public static String TOOLS = "Tools";
    public static String EMAIL = "email";
    public static String CATEGORIES = "Categories";
    public static String CAST = "Cast";
    public static String MOVIES = "Movies";
    public static String INTERESTED = "Interested";
    public static String LOVES = "Loves";
    public static String VERSION = "version";
    public static String PRIVACY_POLICY = "privacyPolicy";
    public static String BASIC = "basic";
    public static String USER_NAME = "username";
    public static String PROFILE_IMAGE = "profileImage";
    public static String EMPTY = "";
    public static String SPACE = " ";
    public static String TIMESTAMP = "timestamp";
    public static String ID = "id";
    public static String IMAGE = "image";
    public static String SLIDER_SHOW = "SliderShow";
    public static String PUBLISHER = "publisher";
    public static String COMMENTS = "Comments";
    public static String NULL = "null";
    public static String FAVORITES = "Favorites";
    public static String VIEWS_COUNT = "viewsCount";
    public static String INTERESTED_COUNT = "interestedCount";
    public static String MOVIES_COUNT = "moviesCount";
    public static String LOVES_COUNT = "lovesCount";
    public static String EDITORS_CHOICE = "editorsChoice";
    public static String NAME = "name";
    public static String CAST_MOVIE = "CastMovie";
    public static String MOVIE_LINK = "movieLink";
    public static String DOT = ".";
    public static int CURRENT_VERSION = 1;
    public static int MIX_SQUARE = 500;
    public static int ZERO = 0;
    public static int ORDER_MAIN = 2; // Here Max Item Show
    public static Boolean searchStatus = false;
    public static Boolean isChange = false;
    //Shared
    public static String PROFILE_ID = "profileId";
    public static String COLOR_OPTION = "color_option";
    public static String CATEGORY_ID = "categoryId";
    public static String SHOW_MORE_TYPE = "showMoreType";
    public static String CATEGORY_NAME = "categoryName";
    public static String SHOW_MORE_NAME = "showMoreName";
    public static String SHOW_MORE_BOOLEAN = "showMoreBoolean";
    public static String FB_ID = "";
    public static String WEB_SITE = "";
    public static String CAST_ID = "castId";
    public static String CAST_NAME = "castName";
    public static String CAST_ABOUT = "castAbout";
    public static String CAST_IMAGE = "castImage";
    public static String MOVIE_ID = "movieId";
    public static String COMMENT = "comment";
    //Other
    public static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    public static final FirebaseUser FIREBASE_USER = AUTH.getCurrentUser();
    public static final String FirebaseUserUid = FIREBASE_USER.getUid();
}