/*******************************************************************************
 *     This file is part of AndFChat.
 * 
 *     AndFChat is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     AndFChat is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with AndFChat.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/


package com.andfchat.core.connection;

/**
 * Due to asynchronous task-managing, sometimes (especially with networking stuff) things have to be done in other threads.
 * This is the helper-class for FlistHttpClient to handle the asynchrony used for http-posts login/bookmarking.
 *
 * @author AndFChat
 */
public abstract class FeedbackListener {
    public abstract void onResponse(String response);
    public abstract void onError(Exception ex);
}
