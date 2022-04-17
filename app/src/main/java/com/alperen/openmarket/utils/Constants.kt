package com.alperen.openmarket.utils

/**
 * Created by Alperen on 22.10.2021.
 */
object Constants {
    const val ONBOARDING_1 = "Open Market uygulamasına hoş geldin. Bu uygulama ile diğer kullanıcıların ürünlerini kolayca satın alabilir, ayrıca kendi ürünlerini de satışa koyarak para kazanabilirsin"
    const val ONBOARDING_2 = "İster ürünlerini doğrudan fiyat belirleyerek, istersen de açık artırma yolu ile satışa sunabilirsin. Tercih tamamen senin"
    const val ONBOARDING_3 = "Sen de üye olup hesabını onayladıktan sonra kolayca ürün alıp satmaya başla\n\nSağa kaydır"

    const val ONBOARDING_LAUNCHED = "onboardingLaunched"
    const val APP_INIT = "isAppInitialized"
    const val FORGET_ME = "forgetMe"

    const val FIELD_REQUIRED = "Bu alan doldurulmalı"
    const val PASSWORD_REQUIRED = "E-mail ve parola değişikliği için eski parola girilmelidir"
    const val FIELD_MISSING = "Lütfen bu alanı tamamen doldurunuz"
    const val PRODUCT_ADDED = "Ürününüz başarıyla satışa sunuldu"
    const val PRODUCT_PURCHASED = "Ürününüz başarıyla satın alındı"
    const val DELETE_DENIED = "Satılan ürün silinemez"
    const val EDIT_DENIED = "Satılan ürün düzenlenemez"
    const val EMAIL_REQUIRED = "Sıfırlama e-maili için lütfen bu alanı doldurunuz"
    const val FIELDS_BE_SAME = "Alanlar aynı değil"
    const val REGISTER_SUCCESS = "Başarıyla kayıt oldunuz. E-mailinize gelen onay linki ile hesabınızı onaylayıp giriş yapabilirsiniz"
    const val RESET_MAIL_SUCCESS = "Sıfırlama e-maili başarıyla gönderildi"
    const val VERIFICATION_REQUIRED = "Giriş yapabilmeniz için e-mailinize gelen onay linki ile hesabınızı onaylamanız gerekli"
    const val IMAGE_REQUIRED = "Mutlaka bir resim eklemelisiniz"
    const val PRODUCT_PROPERTIES_REQUIRED = "Ürün özelliklerinin tamamı belirlenmelidir"
    const val PROFILE_PHOTO_CHANGE = "Profil resminiz başarıyla güncellendi"
    const val NOTHING_CHANGED = "Hiçbir değişiklik yapılmadı"
    const val UPDATE_SUCCESS = "Bilgileriniz başarıyla güncellendi"
    const val PRODUCT_UPDATED = "Ürününüz başarıyla güncellendi"
    const val UPDATE_SUCCESS_WITH_LOGOUT = "Bilgileriniz başarıyla güncellendi. Yeniden giriş yapmanız gerekiyor"
    const val PRODUCT_REMOVED = "Ürününüz başarıyla silindi"
    const val CARD_ADDED = "isimli kartınız başarıyla tanımlandı"
    const val CARD_REMOVED = "isimli kartınız başarıyla silindi"
    const val CARD_UPDATED = "isimli kartınız başarıyla güncellendi"
    const val AUCTION_MESSAGE = "Başlangıç fiyatı: Açık artırmanın başlangıç fiyatını belirler\n" +
            "Artırma miktarı: Başlangıç fiyatı üzerine kaçar kaçar artırma yapılacağını belirler\n" +
            "Tarih ve Saat: Açık artırmanın bitiş tarihi ve saatini belirler"
    const val AUCTION_DATE_REQUIRED = "Bitiş tarihi belirlenmelidir"
    const val AUCTION_TIME_REQUIRED = "Bitiş saati belirlenmelidir"
    const val OFFER_MADE = "Teklifiniz başarıyla verildi"
    const val SMALL_OFFER = "Teklifiniz mevcut fiyattan düşük olamaz"

    val smallIncrement = arrayOf("1","2","3","4","5","6","7","8","9","10")
    val mediumIncrement = arrayOf("5","10","15","20","25","30","35","40","45","50")
    val largeIncrement = arrayOf("50","100","150","200","250","300","350","400","450","500")

    const val OK = "Tamam"
    const val SUCCESS = "Başarılı"
    const val SUCCESS_WITH_LOGOUT = "Başarılı. Çıkış yapılması gerekli"
    const val PROCESSING = "İşleniyor"
}