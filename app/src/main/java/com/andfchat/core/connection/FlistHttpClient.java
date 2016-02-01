package com.andfchat.core.connection;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by AndFChat on 06.04.2015.
 */
public interface FlistHttpClient {

    @FormUrlEncoded
    @POST("/json/getApiTicket.php")
    Call<LoginData> logIn(@Field("account") String account, @Field("password") String password);

    @POST("/json/api/bookmark-add.php")
    Call<Object> addBookmark(@Query("account") String account, @Query("ticket") String ticket, @Query("name") String name);

    @POST("/json/api/bookmark-remove.php")
    Call<Object> removeBookmark(@Query("account") String account, @Query("ticket") String ticket, @Query("name") String name);

    public class LoginData {
        private List<String> characters;

        private String ticket;
        private String error;
        private String default_character;
        private List<Friend> friends;
        private List<Bookmark> bookmarks;
        private List<Ignored> ignores;

        public List<String> getCharacters() {
            return characters;
        }

        public String getTicket() {
            return ticket;
        }

        public String getError() {
            return error;
        }

        public List<Friend> getFriends() {
            return friends;
        }

        public List<Bookmark> getBookmarks() {
            return bookmarks;
        }

        public List<Ignored> getIgnores() {
            return ignores;
        }

        public String getDefaultCharacter() {
            return default_character;
        }

        public class Friend {
            private String source_name;
            private String dest_name;

            public String getFriend() {
                return source_name;
            }

            public String getCharacter() {
                return dest_name;
            }
        }

        public class Bookmark {
            private String name;

            public String getName() {
                return name;
            }
        }

        public class Ignored {
            private String name;

            public String getName() {
                return name;
            }
        }
    }
}