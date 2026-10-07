# İK Portal

Çalışan self-servis portalı: **giriş yap → bilgilerini gir → izin talebi oluştur**.
Spring Boot + Thymeleaf (HTML web arayüzü) + PostgreSQL. IntelliJ IDEA'da doğrudan açılır.

## Teknolojiler

| | |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.5 (web, thymeleaf, data-jpa, validation) |
| Veritabanı | PostgreSQL 18 |
| Şablon | Thymeleaf (sunucu taraflı HTML) |
| Build | Maven (`mvnw` sarmalayıcı dahil) |

## Sayfalar

| Yol | Açıklama |
|---|---|
| `/register` | Hesap oluşturma (e-posta + şifre) |
| `/login` | E-posta ve şifre kontrolü ile giriş |
| `/` | Özet: kalan yıllık izin, kıdem, çalıştığı yer, maaş, son talepler |
| `/profile` | **Bilgilerim** — kişisel, iş ve maaş bilgileri. Bir kez girilir, veritabanında saklanır, `Düzenle` ile güncellenir |
| `/leaves` | **İzinlerim** — yeni talep (tür, başlangıç, bitiş, açıklama) + talep geçmişi |
| `/logout` | Oturumu kapatır |

Giriş yapılmadan `/`, `/profile`, `/leaves` adreslerine erişilemez; `AuthInterceptor` giriş sayfasına yönlendirir.

## Veritabanı tabloları

Tablolar ilk çalıştırmada Hibernate tarafından otomatik oluşturulur (`ddl-auto=update`).
Veritabanı (`ikportal`) da yoksa uygulama açılırken `DatabaseBootstrap` tarafından oluşturulur.

* `app_user` — e-posta, şifre hash'i (PBKDF2-HmacSHA256, 120.000 tur + rastgele salt), görünen ad
* `employee_profile` — ad/soyad, TC, doğum tarihi, telefon, adres, şehir, medeni durum, kan grubu, çalıştığı yer, departman, pozisyon, yönetici, lokasyon, çalışma şekli, işe başlama tarihi, maaş, para birimi, banka, IBAN
* `leave_request` — izin türü, başlangıç, bitiş, iş günü, takvim günü, açıklama, durum, talep/karar zamanı

Şifreler hiçbir zaman düz metin saklanmaz.

## Çalıştırma

### 1. PostgreSQL'i başlat

Bu makinede PostgreSQL **servis olarak kayıtlı değil**, bu yüzden elle başlatılır:

```bash
scripts\veritabani-baslat.cmd
```

Cluster dizini: `C:\Users\ranae\pgdata` · kullanıcı `postgres` · şifre `postgres` · port `5432`
(yalnızca `localhost` dinlenir).

Kalıcı Windows servisi isterseniz **yönetici** PowerShell'de:

```bash
& "C:\Program Files\PostgreSQL\18\bin\pg_ctl.exe" register -N postgresql-x64-18 -D "C:\Users\ranae\pgdata" -S auto
```

### 2. Uygulamayı başlat

IntelliJ'de `IkPortalApplication` sınıfını çalıştırın, ya da:

```bash
scripts\uygulamayi-baslat.cmd
```

Sonra tarayıcıdan: **http://localhost:8080**

İlk kullanımda `Kayıt olun` ile hesabınızı oluşturun, giriş yapın, `Bilgilerim` sayfasını doldurun.

### Veritabanı şifresini değiştirmek

`src/main/resources/application.properties` dosyasını düzenleyin veya ortam değişkeni verin:

```bash
set DB_PASSWORD=yeni_sifre
```

## İş kuralları

* **İş günü hesabı** — izin gün sayısı hafta sonları hariç tutularak hesaplanır.
* **Çakışma kontrolü** — aynı tarih aralığında bekleyen/onaylı ikinci bir talep oluşturulamaz.
* **Yıllık izin hakkı** — 4857 sayılı İş Kanunu m.53'e göre kıdemden hesaplanır:
  1–5 yıl 14 gün, 5–15 yıl 20 gün, 15+ yıl 26 gün; 18 yaş altı / 50 yaş üstü için taban 20 gün.
  Kalan bakiye yalnızca **Yıllık İzin** türündeki taleplerden düşülür.
* **İptal** — yalnızca `Onay Bekliyor` durumundaki talepler iptal edilebilir.
* **Onayla / Reddet** — tek kullanıcılı demo akışı olduğu için talebi kullanıcı kendisi
  sonuçlandırabilir. Gerçek bir yönetici onay akışı eklenecekse `LeaveService.decide`
  metodu rol kontrolüyle sınırlandırılmalıdır.

## Proje yapısı

```
src/main/java/com/pmt/ikportal/
├── IkPortalApplication.java
├── config/     WebConfig (interceptor), DatabaseBootstrap (veritabanını oluşturur)
├── controller/ Auth, Dashboard, Profile, Leave
├── domain/     AppUser, EmployeeProfile, LeaveRequest, LeaveType, LeaveStatus
├── repository/ Spring Data JPA arayüzleri
├── service/    AuthService, ProfileService, LeaveService, PasswordHasher, LeaveBalance
└── web/        SessionKeys, AuthInterceptor, BusinessException, GlobalControllerAdvice

src/main/resources/
├── templates/  login, register, dashboard, profile, leaves + fragments/layout
├── static/css/ app.css
└── application.properties
```
