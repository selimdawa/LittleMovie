package com.flatcode.littlemovieadmin.Unit;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class DATA {
    //Database
    public static String USERS = "Users";
    public static String TOOLS = "Tools";
    public static String CATEGORIES = "Categories";
    public static String CAST = "Cast";
    public static String MOVIES = "Movies";
    public static String INTERESTED = "Interested";
    public static String COMMENTS = "Comments";
    public static String LOVES = "Loves";
    public static String PRIVACY_POLICY = "privacyPolicy";
    public static String DESCRIPTION = "description";
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
    public static String CATEGORY = "category";
    public static String ABOUT_MY = "aboutMy";
    public static String NULL = "null";
    public static String FAVORITES = "Favorites";
    public static String VIEWS_COUNT = "viewsCount";
    public static String CAST_COUNT = "castCount";
    public static String INTERESTED_COUNT = "interestedCount";
    public static String MOVIES_COUNT = "moviesCount";
    public static String LOVES_COUNT = "lovesCount";
    public static String EDITORS_CHOICE = "editorsChoice";
    public static String NAME = "name";
    public static String YEAR = "year";
    public static String CAST_MOVIE = "CastMovie";
    public static String DOT = ".";
    public static ArrayList<String> castMovie = new ArrayList<>();
    public static ArrayList<String> castMovieOld = new ArrayList<>();
    public static int MIX_SQUARE = 500;
    public static int MIX_VIDEO_X = 400;
    public static int MIX_VIDEO_Y = 560;
    public static int MIX_SLIDER_X = 680;
    public static int MIX_SLIDER_Y = 360;
    public static int ZERO = 0;
    public static int MIN_YEAR = 1940;
    public static int MAX_YEAR = 2022;
    public static Boolean searchStatus = false;
    public static Boolean isChange = false;
    //Shared
    public static String PROFILE_ID = "profileId";
    public static String COLOR_OPTION = "color_option";
    public static String EDITORS_CHOICE_ID = "editorsChoiceId";
    public static String CATEGORY_ID = "categoryId";
    public static String CAST_ID = "castId";
    public static String CAST_NAME = "castName";
    public static String CAST_ABOUT = "castAbout";
    public static String CAST_IMAGE = "castImage";
    public static String DURATION = "duration";
    public static String MOVIE_LINK = "movieLink";
    public static String OLD_ID = "oldId";
    public static String CATEGORY_NAME = "categoryName";
    public static String MOVIE_ID = "movieId";
    public static String COMMENT = "comment";
    public static String MOVIE = "Movie";
    public static ArrayList<String> castList;
    public static ArrayList<String> movieList;
    //Other
    public static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    public static final FirebaseUser FIREBASE_USER = AUTH.getCurrentUser();
    public static final String FirebaseUserUid = FIREBASE_USER.getUid();
}