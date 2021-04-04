package com.weather.photo.remoteConnection

object URL {

    fun getLoginUrl(): String {
        var url = "login"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getLogoutUrl(): String {
        var url = "logout"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getHomeUrl(): String {
        var url = "home"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getSocailLogin(): String {
        var url = "social_check"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getCategoriesUrl(): String {
        var url = "categories"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getRegisterUrl(): String {
        var url = "register"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getResetPasswordUrl(): String {
        var url = "reset_password"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getUpdatePasswordUrl(): String {
        var url = "user/update_password"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getUpdateProfileUrl(): String {
        var url = "user/update"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getCategoryItemsUrl(): String {
        var url = "items"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getSocialRegisterUrl(): String {
        var url = "social_register"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getUserUrl(): String {
        var url = "user"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getMyAddressesUrl(): String {
        var url = "address"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getAddressesDefaultUrl(id: String): String {
        var url = "address/$id/make_default"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getDeleteAddressesDefaultUrl(id: String): String {
        var url = "address/$id/delete"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getAddRemoveFavouriteUrl(id: String): String {
        var url = "items/$id/wish"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getAutoCompleteUrl(): String {
        var url = "items/autocomplete"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getCitiesUrl(): String {
        var url = "city"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getDistrictsUrl(): String {
        var url = "district"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }


    fun getItemDetailsUrl(id: String): String {
        var url = "items/$id"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getAboutUrl(): String {
        var url = "about"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getTermsUrl(): String {
        var url = "terms"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getFAQsUrl(): String {
        var url = "faqs"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getFAQDetailsUrl(id: String): String {
        var url = "faqs/$id"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getContactUrl(): String {
        var url = "contact"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getNotifyItemUrl(id: String): String {
        var url = "items/$id/notify"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getCheckCartUrl(): String {
        var url = "cart/check"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getCheckCouponUrl(): String {
        var url = "cart/check_coupon"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getAddOrderUrl(): String {
        var url = "order"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getOrderUrl(id: String): String {
        var url = "order/$id"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }


    fun getTicketsUrl(): String {
        var url = "ticket"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getTicketUrl(id: String): String {
        var url = "ticket/$id"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getAddReplyTicketUrl(id: String): String {
        var url = "ticket/$id/reply"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

}