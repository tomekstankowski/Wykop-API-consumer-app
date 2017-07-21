package com.tomaszstankowski.wykopapi.service

import javax.inject.Inject
import javax.inject.Named


/**
 * Wrapper for Webservice instantiated by Retrofit.
 */
class WykopService @Inject constructor(val webservice: Webservice, @Named("appkey") val appkey: String) {

    fun getLink(id: Int) = webservice.getLink(id, appkey)

    fun getPromotedLinks(page: Int) = webservice.getPromotedLinks(appkey, page)

    fun getComments(linkId: Int) = webservice.getComments(appkey, linkId)
}