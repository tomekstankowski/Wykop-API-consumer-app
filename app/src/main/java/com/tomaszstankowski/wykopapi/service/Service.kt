package com.tomaszstankowski.wykopapi.service

import javax.inject.Inject
import javax.inject.Named


/**
 * Wrapper for WykopAPI instantiated by Retrofit.
 */
class Service @Inject constructor(val wykopAPI: WykopAPI, @Named("appkey") val appkey: String) {

    fun getLink(id: Int) = wykopAPI.getLink(id, appkey)

    fun getPromotedLinks(page: Int) = wykopAPI.getPromotedLinks(appkey, page)

    fun getComments(linkId: Int) = wykopAPI.getComments(appkey, linkId)

    fun getUser(username: String) = wykopAPI.getUser(username, appkey)
}