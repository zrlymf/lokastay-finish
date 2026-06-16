package com.example.lokastay.data

import com.example.lokastay.data.entity.Favorite
import com.example.lokastay.data.entity.Review
import com.example.lokastay.data.entity.User
import com.example.lokastay.data.entity.Villa

object DummyData {

    val users = listOf(
        User(name = "Prinka Shafa", email = "prinka01@gmail.com", phone = "081234560001", password = "123456", memberNumber = "LS-2026-001", currentPoints = 1500, accumulatedPoints = 5500, memberTier = "Platinum"),
        User(name = "Zabrina Zerlynda", email = "zerly02@gmail.com", phone = "081234560002", password = "123456", memberNumber = "LS-2026-002", currentPoints = 500, accumulatedPoints = 2500, memberTier = "Gold"),
        User(name = "Nabila Putri", email = "nabila03@gmail.com", phone = "081234560003", password = "123456", memberNumber = "LS-2026-003", currentPoints = 150, accumulatedPoints = 600, memberTier = "Silver"),
        User(name = "Raka Ananta", email = "raka04@gmail.com", phone = "081234560004", password = "123456", memberNumber = "LS-2026-004", currentPoints = 0, accumulatedPoints = 0, memberTier = "Classic"),
        User(name = "Dinda Permata", email = "dinda05@gmail.com", phone = "081234560005", password = "123456", memberNumber = "LS-2026-005", currentPoints = 0, accumulatedPoints = 0, memberTier = "Classic"),
        User(name = "Rizky Saputra", email = "rizky06@gmail.com", phone = "081234560006", password = "123456", memberNumber = "LS-2026-006", currentPoints = 200, accumulatedPoints = 1200, memberTier = "Silver"),
        User(name = "Alya Putri", email = "alya07@gmail.com", phone = "081234560007", password = "123456", memberNumber = "LS-2026-007", currentPoints = 0, accumulatedPoints = 0, memberTier = "Classic"),
        User(name = "Fajar Ramadhan", email = "fajar08@gmail.com", phone = "081234560008", password = "123456", memberNumber = "LS-2026-008", currentPoints = 0, accumulatedPoints = 0, memberTier = "Classic"),
        User(name = "Mira Lestari", email = "mira09@gmail.com", phone = "081234560009", password = "123456", memberNumber = "LS-2026-009", currentPoints = 800, accumulatedPoints = 3000, memberTier = "Gold"),
        User(name = "Bagas Wijaya", email = "bagas10@gmail.com", phone = "081234560010", password = "123456", memberNumber = "LS-2026-010", currentPoints = 0, accumulatedPoints = 0, memberTier = "Classic")
    )

    val villas = listOf(
        Villa(
            name = "Villa Puncak Asri",
            location = "Jl. Raya Puncak KM 84, Cisarua, Kabupaten Bogor, Jawa Barat",
            pricePerNight = 7200000.0,
            rating = 4.9f,
            imageUrl = "villa_1",
            description = "Experience the crisp mountain air in this charming retreat. Surrounded by lush tea plantations, it offers breathtaking views of the majestic Mount Salak and features an expansive, manicured garden perfect for family picnics.",
            maxGuest = 25,
            hasWifi = true, hasPool = false, hasAc = false, hasLaundry = true, hasTv = true, hasGym = false, hasHeater = true, hasParking = true, hasBbq = true, hasBathtub = true, hasGarden = true, hasKitchen = true,
            imageGallery = listOf("villa_1_a", "villa_1_b", "villa_1_c", "villa_1_d")
        ),
        Villa(
            name = "Villa Dago Green",
            location = "Jl. Bukit Dago Utara No. 12, Coblong, Kota Bandung, Jawa Barat",
            pricePerNight = 3800000.0,
            rating = 4.6f,
            imageUrl = "villa_2",
            description = "A modern sanctuary nestled in the cool highlands of Dago. Enjoy panoramic city light views from the spacious balcony at night, while being just a stone's throw away from trendy cafes and art galleries.",
            maxGuest = 8,
            hasWifi = true, hasPool = true, hasAc = true, hasLaundry = true, hasTv = true, hasGym = false, hasHeater = true, hasParking = true, hasBbq = true, hasBathtub = false, hasGarden = true, hasKitchen = true,
            imageGallery = listOf("villa_2_a", "villa_2_b", "villa_2_c", "villa_2_d")
        ),
        Villa(
            name = "Villa Batu Garden",
            location = "Jl. Abdul Gani Atas No. 45, Ngaglik, Kota Batu, Jawa Timur",
            pricePerNight = 1700000.0,
            rating = 4.6f,
            imageUrl = "villa_3",
            description = "An ideal family getaway situated close to Museum Angkut and Jatim Park. This property boasts a beautifully landscaped garden, an outdoor BBQ pit for evening gatherings, and stunning views of Mount Arjuno.",
            maxGuest = 8,
            hasWifi = true, hasPool = true, hasAc = false, hasLaundry = true, hasTv = true, hasGym = false, hasHeater = true, hasParking = true, hasBbq = true, hasBathtub = true, hasGarden = true, hasKitchen = true,
            imageGallery = listOf("villa_3_a", "villa_3_b", "villa_3_c", "villa_3_d")
        ),
        Villa(
            name = "Villa Ubud Harmony",
            location = "Jl. Raya Sayan, Kedewatan, Gianyar, Bali",
            pricePerNight = 3750000.0,
            rating = 5.0f,
            imageUrl = "villa_4",
            description = "Immerse yourself in tranquility overlooking the lush Ayung River valley. This tropical haven features a private plunge pool and traditional Balinese architecture, perfect for a romantic, secluded escape.",
            maxGuest = 12,
            hasWifi = true, hasPool = true, hasAc = true, hasLaundry = true, hasTv = true, hasGym = false, hasHeater = false, hasParking = true, hasBbq = false, hasBathtub = true, hasGarden = true, hasKitchen = true,
            imageGallery = listOf("villa_4_a", "villa_4_b", "villa_4_c", "villa_4_d")
        ),
        Villa(
            name = "Villa Kuta Sunset",
            location = "Jl. Pantai Kuta No. 88, Kuta, Kabupaten Badung, Bali",
            pricePerNight = 1550000.0,
            rating = 4.8f,
            imageUrl = "villa_5",
            description = "Strategically located just steps away from the iconic golden sands of Kuta Beach. Experience the vibrant nightlife, world-class surfing spots, and witness spectacular, fiery sunsets right from your doorstep.",
            maxGuest = 8,
            hasWifi = true, hasPool = true, hasAc = true, hasLaundry = true, hasTv = true, hasGym = true, hasHeater = false, hasParking = true, hasBbq = true, hasBathtub = true, hasGarden = false, hasKitchen = true,
            imageGallery = listOf("villa_5_a", "villa_5_b", "villa_5_c", "villa_5_d")
        ),
        Villa(
            name = "Villa Lembang Breeze",
            location = "Jl. Tangkuban Perahu KM 2, Lembang, Kabupaten Bandung Barat, Jawa Barat",
            pricePerNight = 1800000.0,
            rating = 4.7f,
            imageUrl = "villa_6",
            description = "A spacious mountain lodge offering refreshing pine-scented breezes. It features a cozy outdoor fire pit area and is conveniently located near the famous Floating Market and Farmhouse Lembang.",
            maxGuest = 12,
            hasWifi = true, hasPool = true, hasAc = false, hasLaundry = true, hasTv = true, hasGym = false, hasHeater = true, hasParking = true, hasBbq = true, hasBathtub = true, hasGarden = true, hasKitchen = true,
            imageGallery = listOf("villa_6_a", "villa_6_b", "villa_6_c", "villa_6_d")
        ),
        Villa(
            name = "Villa Bromo View",
            location = "Jl. Raya Bromo No. 15, Tosari, Kabupaten Pasuruan, Jawa Timur",
            pricePerNight = 1300000.0,
            rating = 4.8f,
            imageUrl = "villa_7",
            description = "Your ultimate basecamp for adventure. Wake up to the dramatic, otherworldly landscape of the Tengger Caldera and catch the legendary golden sunrise over Mount Bromo from this comfortable alpine retreat.",
            maxGuest = 4,
            hasWifi = true, hasPool = false, hasAc = false, hasLaundry = true, hasTv = true, hasGym = false, hasHeater = true, hasParking = true, hasBbq = true, hasBathtub = true, hasGarden = true, hasKitchen = true,
            imageGallery = listOf("villa_7_a", "villa_7_b", "villa_7_c", "villa_7_d")
        ),
        Villa(
            name = "Villa Ciater Warm",
            location = "Jl. Raya Ciater, Sariater, Kabupaten Subang, Jawa Barat",
            pricePerNight = 900000.0,
            rating = 3.7f,
            imageUrl = "villa_8",
            description = "The ultimate healing destination nestled in the tea estates. This property is located right next to the famous natural hot springs, offering a therapeutic and relaxing atmosphere amid the cool mountain climate.",
            maxGuest = 6,
            hasWifi = true, hasPool = true, hasAc = false, hasLaundry = true, hasTv = true, hasGym = false, hasHeater = true, hasParking = true, hasBbq = true, hasBathtub = true, hasGarden = true, hasKitchen = true,
            imageGallery = listOf("villa_8_a", "villa_8_b", "villa_8_c", "villa_8_d")
        ),
        Villa(
            name = "Villa Trawas Hills",
            location = "Jl. Raya Trawas KM 5, Trawas, Kabupaten Mojokerto, Jawa Timur",
            pricePerNight = 950000.0,
            rating = 3.9f,
            imageUrl = "villa_9",
            description = "A serene hideaway tucked between Mount Penanggungan and Mount Welirang. Enjoy the invigorating fresh air, a massive green courtyard, and easy access to picturesque hidden waterfalls.",
            maxGuest = 2,
            hasWifi = true, hasPool = false, hasAc = true, hasLaundry = true, hasTv = true, hasGym = false, hasHeater = true, hasParking = true, hasBbq = true, hasBathtub = false, hasGarden = true, hasKitchen = true,
            imageGallery = listOf("villa_9_a", "villa_9_b", "villa_9_c", "villa_9_d")
        ),
        Villa(
            name = "Villa Anyer Bay",
            location = "Jl. Raya Karang Bolong KM 135, Anyer, Kabupaten Serang, Banten",
            pricePerNight = 1750000.0,
            rating = 3.9f,
            imageUrl = "villa_10",
            description = "A stunning beachfront property offering unobstructed views of the Sunda Strait. Perfect for weekend staycations, where you can fall asleep to the sound of crashing waves and spot the distant Krakatoa volcano.",
            maxGuest = 12,
            hasWifi = true, hasPool = true, hasAc = true, hasLaundry = true, hasTv = true, hasGym = false, hasHeater = false, hasParking = true, hasBbq = true, hasBathtub = true, hasGarden = true, hasKitchen = true,
            imageGallery = listOf("villa_10_a", "villa_10_b", "villa_10_c", "villa_10_d")
        ),
        Villa(
            name = "Villa Sentul Forest",
            location = "Jl. Babakan Madang, Sentul City, Kabupaten Bogor, Jawa Barat",
            pricePerNight = 1250000.0,
            rating = 4.5f,
            imageUrl = "villa_11",
            description = "A peaceful escape hidden within a verdant pine forest. Despite its secluded feel, it offers quick access to the Sentul highland eco-tourism spots and cascading waterfalls.",
            maxGuest = 6,
            hasWifi = true, hasPool = true, hasAc = true, hasLaundry = true, hasTv = true, hasGym = true, hasHeater = false, hasParking = true, hasBbq = true, hasBathtub = false, hasGarden = true, hasKitchen = true,
            imageGallery = listOf("villa_11_a", "villa_11_b", "villa_11_c", "villa_11_d")
        ),
        Villa(
            name = "Villa Seminyak Calm",
            location = "Jl. Kayu Aya No. 22, Seminyak, Kabupaten Badung, Bali",
            pricePerNight = 2600000.0,
            rating = 4.7f,
            imageUrl = "villa_12",
            description = "A premium, minimalist villa located in the heart of Bali's chicest district. You are only minutes away from high-end boutiques, renowned beach clubs like Potato Head, and exquisite fine-dining restaurants.",
            maxGuest = 16,
            hasWifi = true, hasPool = true, hasAc = true, hasLaundry = true, hasTv = true, hasGym = true, hasHeater = true, hasParking = true, hasBbq = true, hasBathtub = true, hasGarden = true, hasKitchen = true,
            imageGallery = listOf("villa_12_a", "villa_12_b", "villa_12_c", "villa_12_d")
        ),
        Villa(
            name = "Villa Dieng Sky",
            location = "Jl. Telaga Warna, Kejajar, Kabupaten Wonosobo, Jawa Tengah",
            pricePerNight = 1000000.0,
            rating = 4.6f,
            imageUrl = "villa_13",
            description = "Experience life above the clouds in this cozy highland cabin. Located near the mystical Telaga Warna and ancient Hindu temples, it's the perfect spot to bundle up and enjoy the chilly, misty atmosphere.",
            maxGuest = 6,
            hasWifi = true, hasPool = false, hasAc = false, hasLaundry = true, hasTv = true, hasGym = false, hasHeater = true, hasParking = true, hasBbq = true, hasBathtub = true, hasGarden = true, hasKitchen = true,
            imageGallery = listOf("villa_13_a", "villa_13_b", "villa_13_c", "villa_13_d")
        ),
        Villa(
            name = "Villa Yogyakarta Heritage",
            location = "Jl. Kaliurang KM 22, Pakem, Kabupaten Sleman, DI Yogyakarta",
            pricePerNight = 1450000.0,
            rating = 4.5f,
            imageUrl = "villa_14",
            description = "A beautiful blend of traditional Javanese Joglo architecture and modern comforts. Nestled on the cool slopes of Mount Merapi, the property features a lush, tranquil garden perfect for meditation.",
            maxGuest = 8,
            hasWifi = true, hasPool = true, hasAc = true, hasLaundry = true, hasTv = true, hasGym = false, hasHeater = false, hasParking = true, hasBbq = true, hasBathtub = true, hasGarden = true, hasKitchen = true,
            imageGallery = listOf("villa_14_a", "villa_14_b", "villa_14_c", "villa_14_d")
        ),
        Villa(
            name = "Villa Guci Natural",
            location = "Jl. Objek Wisata Guci, Bumijawa, Kabupaten Tegal, Jawa Tengah",
            pricePerNight = 1900000.0,
            rating = 4.6f,
            imageUrl = "villa_15",
            description = "A rustic mountain retreat positioned near the slopes of Mount Slamet. Famous for its proximity to public thermal baths, it offers a rejuvenating experience surrounded by dense, green pine forests.",
            maxGuest = 12,
            hasWifi = true, hasPool = true, hasAc = false, hasLaundry = true, hasTv = true, hasGym = false, hasHeater = true, hasParking = true, hasBbq = true, hasBathtub = false, hasGarden = true, hasKitchen = true,
            imageGallery = listOf("villa_15_a", "villa_15_b", "villa_15_c", "villa_15_d")
        )
    )

    val reviews = listOf(
        Review(villaId = 1, username = "Nabila Putri", comment = "Tempatnya bersih dan view bagus. Cocok buat kabur dari Jakarta.", rating = 4.8f),
        Review(villaId = 1, username = "John Miller", comment = "Amazing mountain views! The weather was perfectly chilly.", rating = 4.5f),
        Review(villaId = 1, username = "Raka Ananta", comment = "Cocok untuk keluarga dan suasananya tenang.", rating = 4.6f),
        Review(villaId = 1, username = "Sarah Jenkins", comment = "Nice garden for the kids to play, but the WiFi was a bit slow.", rating = 4.0f),
        Review(villaId = 1, username = "Budi Santoso", comment = "Pemandangannya juara, sayangnya jalan masuk agak sempit.", rating = 4.2f),
        Review(villaId = 1, username = "Alice Chang", comment = "Lovely stay with a great atmosphere for a family picnic.", rating = 4.7f),

        Review(villaId = 2, username = "Alya Putri", comment = "Interiornya cantik dan nyaman. Dekat kemana-mana.", rating = 4.9f),
        Review(villaId = 2, username = "David Chen", comment = "Stunning city lights at night from the balcony. Highly recommend!", rating = 4.8f),
        Review(villaId = 2, username = "Bagas Wijaya", comment = "Udara sejuk, akses juga lumayan mudah.", rating = 4.7f),
        Review(villaId = 2, username = "Michael Brown", comment = "Modern design and very close to Dago's best cafes.", rating = 4.6f),
        Review(villaId = 2, username = "Siti Aminah", comment = "Fasilitas lengkap, air panas berfungsi dengan baik.", rating = 4.8f),
        Review(villaId = 2, username = "Emma Smith", comment = "Beautiful sanctuary, but the traffic around Dago was heavy.", rating = 4.4f),

        Review(villaId = 3, username = "Mira Lestari", comment = "Halamannya luas, cocok untuk kumpul keluarga besar.", rating = 4.5f),
        Review(villaId = 3, username = "Sophie Laurent", comment = "Beautiful scenery and very clean property. We enjoyed the BBQ.", rating = 4.7f),
        Review(villaId = 3, username = "Fajar Ramadhan", comment = "Recommended untuk staycation. Dekat Jatim Park.", rating = 4.6f),
        Review(villaId = 3, username = "Daniel Craig", comment = "A great base for exploring Museum Angkut. The garden is well-kept.", rating = 4.8f),
        Review(villaId = 3, username = "Joko Widodo", comment = "Sangat nyaman untuk anak-anak bermain di taman.", rating = 4.7f),
        Review(villaId = 3, username = "Amanda Lee", comment = "The mountain view in the morning is absolutely stunning.", rating = 4.9f),

        Review(villaId = 4, username = "Prinka Shafa", comment = "Sangat estetik dan menenangkan. Cocok buat healing.", rating = 5.0f),
        Review(villaId = 4, username = "James Wilson", comment = "Absolutely magical. The jungle view from the pool is breathtaking.", rating = 5.0f),
        Review(villaId = 4, username = "Dinda Permata", comment = "Private dan suasananya enak banget.", rating = 4.9f),
        Review(villaId = 4, username = "Emily Watson", comment = "A perfect romantic getaway. Traditional yet very comfortable.", rating = 4.8f),
        Review(villaId = 4, username = "Reza Rahadian", comment = "Pelayanan staf sangat ramah, sarapannya juga enak.", rating = 4.9f),
        Review(villaId = 4, username = "Chloe Davis", comment = "Very peaceful and quiet. Truly an escape from the city.", rating = 4.7f),

        Review(villaId = 5, username = "Rizky Saputra", comment = "Lokasi strategis tapi tetap nyaman. Gampang cari makan.", rating = 4.3f),
        Review(villaId = 5, username = "Liam Hemsworth", comment = "Perfect location near the beach. Great for surfing trips.", rating = 4.5f),
        Review(villaId = 5, username = "Zabrina Zerlynda", comment = "Bagus untuk short escape bareng teman-teman.", rating = 4.5f),
        Review(villaId = 5, username = "Noah Williams", comment = "Loved the sunset view, though it gets a bit noisy at night.", rating = 4.4f),
        Review(villaId = 5, username = "Bintang Pratama", comment = "AC dingin, kasur empuk, akses ke pantai cuma jalan kaki.", rating = 4.6f),
        Review(villaId = 5, username = "Olivia Jones", comment = "Great pool to relax after a long day at the beach.", rating = 4.7f),

        Review(villaId = 6, username = "Nabila Putri", comment = "View kebun dan udaranya mantap.", rating = 4.7f),
        Review(villaId = 6, username = "Hannah Schmidt", comment = "Very refreshing pine breeze. The outdoor fire pit was a nice touch.", rating = 4.5f),
        Review(villaId = 6, username = "Alya Putri", comment = "Villa luas, cocok buat rombongan.", rating = 4.8f),
        Review(villaId = 6, username = "Oliver Twist", comment = "Great for big families. Close to all Lembang attractions.", rating = 4.6f),
        Review(villaId = 6, username = "Putri Ayu", comment = "Api unggun di malam hari bikin suasana makin seru.", rating = 4.9f),
        Review(villaId = 6, username = "Dimas Anggara", comment = "Halamannya luas banget, parkiran aman.", rating = 4.7f),

        Review(villaId = 7, username = "Bagas Wijaya", comment = "Sunrise-nya keren banget. Best experience!", rating = 4.9f),
        Review(villaId = 7, username = "Tom Baker", comment = "Unreal view of the caldera right from the window.", rating = 5.0f),
        Review(villaId = 7, username = "Raka Ananta", comment = "Worth it untuk suasana pegunungan.", rating = 4.8f),
        Review(villaId = 7, username = "Lucas Silva", comment = "Cold outside but the heater inside works perfectly.", rating = 4.7f),
        Review(villaId = 7, username = "Rina Melati", comment = "Sangat dekat dengan titik kumpul jeep Bromo.", rating = 4.8f),
        Review(villaId = 7, username = "Amelia Earhart", comment = "An adventurous stay! Loved the alpine vibe.", rating = 4.6f),

        Review(villaId = 8, username = "Mira Lestari", comment = "Nyaman dan dekat wisata air panas.", rating = 4.2f),
        Review(villaId = 8, username = "Sarah Connor", comment = "Good place to relax, the hot springs nearby are fantastic.", rating = 4.0f),
        Review(villaId = 8, username = "Tono Subagyo", comment = "Sangat rekomen untuk orang tua yang butuh terapi air panas.", rating = 4.5f),
        Review(villaId = 8, username = "Kenji Sato", comment = "Very relaxing environment among the tea estates.", rating = 4.5f),
        Review(villaId = 8, username = "Lisa Manoban", comment = "A bit far from the city, but worth the peacefulness.", rating = 4.3f),
        Review(villaId = 8, username = "Andi Firmansyah", comment = "Pemandangan kebun teh dari jendela kamar sangat indah.", rating = 4.4f),

        Review(villaId = 9, username = "Dinda Permata", comment = "Tempatnya adem dan cocok healing.", rating = 4.6f),
        Review(villaId = 9, username = "William King", comment = "Hidden gem! The fresh air and waterfalls nearby were amazing.", rating = 4.5f),
        Review(villaId = 9, username = "Ayu Tingting", comment = "Villa yang sangat luas, anak-anak puas lari-larian.", rating = 4.7f),
        Review(villaId = 9, username = "Ethan Hunt", comment = "A serene escape with a massive courtyard.", rating = 4.8f),
        Review(villaId = 9, username = "Gilang Dirga", comment = "Udara pegunungannya segar banget, bebas polusi.", rating = 4.6f),
        Review(villaId = 9, username = "Mia Wallace", comment = "Lovely stay. We will definitely come back.", rating = 4.5f),

        Review(villaId = 10, username = "Fajar Ramadhan", comment = "View lautnya bagus banget.", rating = 4.8f),
        Review(villaId = 10, username = "Olivia Taylor", comment = "Falling asleep to the sound of crashing waves was so peaceful.", rating = 4.7f),
        Review(villaId = 10, username = "Rachel Adams", comment = "Great beachfront access. Perfect weekend escape.", rating = 4.9f),
        Review(villaId = 10, username = "Surya Saputra", comment = "Fasilitas BBQ di pinggir pantai sangat seru.", rating = 4.6f),
        Review(villaId = 10, username = "Benjamin Button", comment = "The sunset over the Sunda Strait is unforgettable.", rating = 4.8f),
        Review(villaId = 10, username = "Ratna Galih", comment = "Sangat bersih dan stafnya sigap membantu.", rating = 4.7f),

        Review(villaId = 11, username = "Rizky Saputra", comment = "Sentul Forest ini cocok buat short trip bareng keluarga.", rating = 4.4f),
        Review(villaId = 11, username = "Daniel Lee", comment = "A nice quiet forest retreat not too far from the city.", rating = 4.6f),
        Review(villaId = 11, username = "Citra Kirana", comment = "Banyak spot foto instagenic di sekitar villa.", rating = 4.5f),
        Review(villaId = 11, username = "Sophia Loren", comment = "Loved the verdant pine forest surrounding the house.", rating = 4.7f),
        Review(villaId = 11, username = "Eka Putra", comment = "Sinyal internet kadang putus, tapi justru bagus buat digital detox.", rating = 4.3f),
        Review(villaId = 11, username = "Jack Sparrow", comment = "Great eco-tourism spot. Very relaxing.", rating = 4.5f),

        Review(villaId = 12, username = "Prinka Shafa", comment = "Premium dan sangat nyaman. Fasilitas super lengkap.", rating = 5.0f),
        Review(villaId = 12, username = "Alexander Wright", comment = "Luxury at its finest. Walking distance to great beach clubs.", rating = 5.0f),
        Review(villaId = 12, username = "Wira Kusuma", comment = "Desain interiornya juara, kolam renangnya bersih.", rating = 4.9f),
        Review(villaId = 12, username = "Isabella Swan", comment = "Gorgeous minimalist design. Every corner is photogenic.", rating = 4.9f),
        Review(villaId = 12, username = "Dian Sastro", comment = "Harga mahal tapi sangat sepadan dengan kualitasnya.", rating = 4.8f),
        Review(villaId = 12, username = "Mason Mount", comment = "Best villa I've ever stayed in Bali. Highly recommended.", rating = 5.0f),

        Review(villaId = 13, username = "Zabrina Zerlynda", comment = "Dingin banget dan berkabut, asyik buat santai minum teh.", rating = 4.6f),
        Review(villaId = 13, username = "Chris Evans", comment = "Literally a house above the clouds. Make sure to bring warm clothes!", rating = 4.5f),
        Review(villaId = 13, username = "Bayu Skak", comment = "Pemandangan Telaga Warna dari dekat villa bagus pol.", rating = 4.7f),
        Review(villaId = 13, username = "Emma Stone", comment = "Mystical and beautiful. The heater was a lifesaver.", rating = 4.8f),
        Review(villaId = 13, username = "Wahyu Setiawan", comment = "Akses jalan cukup menanjak, pastikan mobil fit.", rating = 4.4f),
        Review(villaId = 13, username = "Leo DiCaprio", comment = "A very unique highland experience in Indonesia.", rating = 4.6f),

        Review(villaId = 14, username = "Alya Putri", comment = "Nuansanya unik dan homey. Joglo-nya kerasa banget.", rating = 4.5f),
        Review(villaId = 14, username = "Robert Downey", comment = "Beautiful blend of modern comfort and Javanese heritage.", rating = 4.7f),
        Review(villaId = 14, username = "Sri Sultan", comment = "Suasana keraton sangat kental, halamannya asri.", rating = 4.6f),
        Review(villaId = 14, username = "Yuki Tanaka", comment = "Very tranquil garden. Perfect place for meditation.", rating = 4.6f),
        Review(villaId = 14, username = "Thomas Shelby", comment = "Great architecture. The cool slope of Merapi was refreshing.", rating = 4.5f),
        Review(villaId = 14, username = "Hadi Suwarno", comment = "Keluarga sangat suka, sarapan tradisionalnya lezat.", rating = 4.8f),

        Review(villaId = 15, username = "Bagas Wijaya", comment = "Murah dan suasananya oke. Sayang agak jauh masuknya.", rating = 4.2f),
        Review(villaId = 15, username = "Elena Rossi", comment = "A true rustic experience. Loved the thermal baths nearby.", rating = 4.4f),
        Review(villaId = 15, username = "Kiki Fatmala", comment = "Hutan pinusnya bikin tenang, kolam air panasnya juara.", rating = 4.5f),
        Review(villaId = 15, username = "Marcus Rashford", comment = "Nice mountain retreat, though the amenities are a bit basic.", rating = 4.1f),
        Review(villaId = 15, username = "Yudi Hermawan", comment = "Cocok buat rombongan bapak-bapak untuk acara gathering.", rating = 4.3f),
        Review(villaId = 15, username = "Anna Kendrick", comment = "Very dense and green environment. Great for hiking.", rating = 4.4f)
    )

    val favorites = listOf(
        Favorite(userId = 1, villaId = 4),
        Favorite(userId = 1, villaId = 10),
        Favorite(userId = 2, villaId = 2),
        Favorite(userId = 2, villaId = 12),
        Favorite(userId = 3, villaId = 1),
        Favorite(userId = 4, villaId = 7),
        Favorite(userId = 5, villaId = 6),
        Favorite(userId = 6, villaId = 3),
        Favorite(userId = 7, villaId = 9),
        Favorite(userId = 8, villaId = 11)
    )
}