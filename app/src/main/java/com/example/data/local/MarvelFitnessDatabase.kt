package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AnnouncementDao
import com.example.data.local.dao.CheckInDao
import com.example.data.local.dao.MemberDao
import com.example.data.local.dao.PaymentDao
import com.example.data.local.dao.PlanDao
import com.example.data.local.entity.AnnouncementEntity
import com.example.data.local.entity.CheckInEntity
import com.example.data.local.entity.MemberEntity
import com.example.data.local.entity.PaymentEntity
import com.example.data.local.entity.PlanEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MemberEntity::class,
        PlanEntity::class,
        PaymentEntity::class,
        CheckInEntity::class,
        AnnouncementEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MarvelFitnessDatabase : RoomDatabase() {

    abstract fun memberDao(): MemberDao
    abstract fun planDao(): PlanDao
    abstract fun paymentDao(): PaymentDao
    abstract fun checkInDao(): CheckInDao
    abstract fun announcementDao(): AnnouncementDao

    companion object {
        @Volatile
        private var INSTANCE: MarvelFitnessDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MarvelFitnessDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MarvelFitnessDatabase::class.java,
                    "marvel_fitness.db"
                )
                .addCallback(MarvelDatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class MarvelDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        seedDatabase(database)
                    }
                }
            }
        }

        suspend fun seedDatabase(database: MarvelFitnessDatabase) {
            val planDao = database.planDao()
            val memberDao = database.memberDao()
            val paymentDao = database.paymentDao()
            val checkInDao = database.checkInDao()
            val announcementDao = database.announcementDao()

            val now = System.currentTimeMillis()
            val dayMs = 86_400_000L

            // 1. Initial Customizable Plans
            val defaultPlans = listOf(
                PlanEntity(
                    id = 1,
                    title = "Basic Monthly",
                    price = 1499.0,
                    durationMonths = 1,
                    features = "Gym Floor Access\nStandard Cardio Zone\nLocker Room & Shower\nFree Wi-Fi",
                    isPopular = false,
                    isActive = true,
                    badgeText = "Starter"
                ),
                PlanEntity(
                    id = 2,
                    title = "Pro Quarterly",
                    price = 3999.0,
                    durationMonths = 3,
                    features = "Gym Floor + Free Weights\nCrossFit & Functional Zone\nBody Composition Analysis\nDiet & Nutrition Consultation\nLocker Room & Steam Bath",
                    isPopular = true,
                    isActive = true,
                    badgeText = "Most Popular"
                ),
                PlanEntity(
                    id = 3,
                    title = "Elite Half-Yearly",
                    price = 7499.0,
                    durationMonths = 6,
                    features = "All Pro Features\n2 Personal Trainer Sessions/mo\nSauna & Steam Access\nFree Marvel Fitness Shaker\nGuest Pass (1/mo)",
                    isPopular = false,
                    isActive = true,
                    badgeText = "Best Value"
                ),
                PlanEntity(
                    id = 4,
                    title = "Marvel Titan Annual",
                    price = 13999.0,
                    durationMonths = 12,
                    features = "Unlimited 24/7 Access\nDedicated Personal Coach\nFull Nutrition & Supplement Plan\nExclusive Marvel Merchandise Kit\nPriority Locker & Unlimited Steam\nFree Freeze/Hold (30 days)",
                    isPopular = false,
                    isActive = true,
                    badgeText = "VIP All-Access"
                )
            )
            planDao.insertAll(defaultPlans)

            // 2. Realistic Members
            val initialMembers = listOf(
                MemberEntity(
                    id = 1,
                    fullName = "Aarav Sharma",
                    phone = "+91 98765 43210",
                    email = "aarav.sharma@example.com",
                    gender = "Male",
                    joinDate = now - (90 * dayMs),
                    planId = 2,
                    planName = "Pro Quarterly",
                    expiryDate = now + (3 * dayMs), // Expiring in 3 days!
                    isActive = true,
                    emergencyContact = "+91 98111 22334",
                    notes = "Focused on hypertrophy and heavy deadlifts.",
                    avatarColorIndex = 0,
                    lastCheckIn = now - (2 * 3600_000L),
                    attendanceCount = 54
                ),
                MemberEntity(
                    id = 2,
                    fullName = "Rhea Kapoor",
                    phone = "+91 98234 56789",
                    email = "rhea.k@example.com",
                    gender = "Female",
                    joinDate = now - (180 * dayMs),
                    planId = 3,
                    planName = "Elite Half-Yearly",
                    expiryDate = now - (2 * dayMs), // Expired 2 days ago!
                    isActive = false,
                    emergencyContact = "+91 98999 11223",
                    notes = "CrossFit athlete, requires reminder for renewal.",
                    avatarColorIndex = 1,
                    lastCheckIn = now - (3 * dayMs),
                    attendanceCount = 88
                ),
                MemberEntity(
                    id = 3,
                    fullName = "Vikramaditya Roy",
                    phone = "+91 98450 12345",
                    email = "vikram.roy@example.com",
                    gender = "Male",
                    joinDate = now - (45 * dayMs),
                    planId = 4,
                    planName = "Marvel Titan Annual",
                    expiryDate = now + (320 * dayMs),
                    isActive = true,
                    emergencyContact = "+91 98700 98700",
                    notes = "Personal trainer package with Coach Rajesh.",
                    avatarColorIndex = 2,
                    lastCheckIn = now - (1 * 3600_000L),
                    attendanceCount = 36
                ),
                MemberEntity(
                    id = 4,
                    fullName = "Priya Nair",
                    phone = "+91 97654 32190",
                    email = "priya.nair@example.com",
                    gender = "Female",
                    joinDate = now - (25 * dayMs),
                    planId = 1,
                    planName = "Basic Monthly",
                    expiryDate = now + (5 * dayMs), // Expiring in 5 days!
                    isActive = true,
                    emergencyContact = "+91 97111 55667",
                    notes = "Morning cardio & functional training.",
                    avatarColorIndex = 3,
                    lastCheckIn = now - (4 * 3600_000L),
                    attendanceCount = 18
                ),
                MemberEntity(
                    id = 5,
                    fullName = "Kabir Mehta",
                    phone = "+91 99123 45678",
                    email = "kabir.m@example.com",
                    gender = "Male",
                    joinDate = now - (60 * dayMs),
                    planId = 2,
                    planName = "Pro Quarterly",
                    expiryDate = now + (28 * dayMs),
                    isActive = true,
                    emergencyContact = "+91 99444 88990",
                    notes = "Strength conditioning & mobility drills.",
                    avatarColorIndex = 4,
                    lastCheckIn = now - (30 * 60_000L),
                    attendanceCount = 42
                ),
                MemberEntity(
                    id = 6,
                    fullName = "Ananya Sen",
                    phone = "+91 98300 76543",
                    email = "ananya.sen@example.com",
                    gender = "Female",
                    joinDate = now - (120 * dayMs),
                    planId = 2,
                    planName = "Pro Quarterly",
                    expiryDate = now - (5 * dayMs), // Expired 5 days ago!
                    isActive = false,
                    emergencyContact = "+91 98300 11111",
                    notes = "Requested student discount renewal.",
                    avatarColorIndex = 5,
                    lastCheckIn = now - (6 * dayMs),
                    attendanceCount = 62
                ),
                MemberEntity(
                    id = 7,
                    fullName = "Rohan Malhotra",
                    phone = "+91 98888 22233",
                    email = "rohan.m@example.com",
                    gender = "Male",
                    joinDate = now - (15 * dayMs),
                    planId = 1,
                    planName = "Basic Monthly",
                    expiryDate = now + (15 * dayMs),
                    isActive = true,
                    emergencyContact = "+91 98888 99999",
                    notes = "Evening weight training.",
                    avatarColorIndex = 0,
                    lastCheckIn = now - (5 * 3600_000L),
                    attendanceCount = 12
                ),
                MemberEntity(
                    id = 8,
                    fullName = "Sneha Patel",
                    phone = "+91 97234 88776",
                    email = "sneha.p@example.com",
                    gender = "Female",
                    joinDate = now - (100 * dayMs),
                    planId = 3,
                    planName = "Elite Half-Yearly",
                    expiryDate = now + (80 * dayMs),
                    isActive = true,
                    emergencyContact = "+91 97234 00000",
                    notes = "Cardio, Zumba, and core strength.",
                    avatarColorIndex = 2,
                    lastCheckIn = now - (3 * 3600_000L),
                    attendanceCount = 74
                )
            )
            memberDao.insertAll(initialMembers)

            // 3. Initial Payment & Renewal Transactions
            val initialPayments = listOf(
                PaymentEntity(
                    id = 1,
                    memberId = 3,
                    memberName = "Vikramaditya Roy",
                    planName = "Marvel Titan Annual",
                    amount = 13999.0,
                    paymentDate = now - (45 * dayMs),
                    paymentMethod = "Card",
                    transactionRef = "TXN_MF_98321",
                    durationMonthsAdded = 12,
                    notes = "Full annual fee paid at front desk"
                ),
                PaymentEntity(
                    id = 2,
                    memberId = 5,
                    memberName = "Kabir Mehta",
                    planName = "Pro Quarterly",
                    amount = 3999.0,
                    paymentDate = now - (60 * dayMs),
                    paymentMethod = "UPI",
                    transactionRef = "UPI_MF_77182",
                    durationMonthsAdded = 3,
                    notes = "Google Pay transaction"
                ),
                PaymentEntity(
                    id = 3,
                    memberId = 8,
                    memberName = "Sneha Patel",
                    planName = "Elite Half-Yearly",
                    amount = 7499.0,
                    paymentDate = now - (100 * dayMs),
                    paymentMethod = "UPI",
                    transactionRef = "UPI_MF_32891",
                    durationMonthsAdded = 6,
                    notes = "PhonePe direct transfer"
                ),
                PaymentEntity(
                    id = 4,
                    memberId = 7,
                    memberName = "Rohan Malhotra",
                    planName = "Basic Monthly",
                    amount = 1499.0,
                    paymentDate = now - (15 * dayMs),
                    paymentMethod = "Cash",
                    transactionRef = "CSH_MF_10042",
                    durationMonthsAdded = 1,
                    notes = "Cash received by receptionist"
                ),
                PaymentEntity(
                    id = 5,
                    memberId = 4,
                    memberName = "Priya Nair",
                    planName = "Basic Monthly",
                    amount = 1499.0,
                    paymentDate = now - (25 * dayMs),
                    paymentMethod = "UPI",
                    transactionRef = "UPI_MF_99182",
                    durationMonthsAdded = 1,
                    notes = "Instant UPI QR scan"
                )
            )
            paymentDao.insertAll(initialPayments)

            // 4. Today's Check-ins
            val initialCheckIns = listOf(
                CheckInEntity(id = 1, memberId = 5, memberName = "Kabir Mehta", checkInTime = now - (30 * 60_000L)),
                CheckInEntity(id = 2, memberId = 3, memberName = "Vikramaditya Roy", checkInTime = now - (1 * 3600_000L)),
                CheckInEntity(id = 3, memberId = 1, memberName = "Aarav Sharma", checkInTime = now - (2 * 3600_000L)),
                CheckInEntity(id = 4, memberId = 8, memberName = "Sneha Patel", checkInTime = now - (3 * 3600_000L)),
                CheckInEntity(id = 5, memberId = 4, memberName = "Priya Nair", checkInTime = now - (4 * 3600_000L))
            )
            checkInDao.insertAll(initialCheckIns)

            // 5. Gym Announcements
            val initialAnnouncements = listOf(
                AnnouncementEntity(
                    id = 1,
                    title = "New Olympic Barbells & Bumper Plates",
                    message = "We have upgraded our powerlifting zone with 4 new competition Olympic barbells and calibrated bumper plates!",
                    date = now - (2 * dayMs),
                    category = "Equipment"
                ),
                AnnouncementEntity(
                    id = 2,
                    title = "Marvel Weekend HIIT Boot Camp",
                    message = "Join Master Trainer Rajesh this Saturday at 7:30 AM for a high-intensity endurance workout. Free for all active members!",
                    date = now - (4 * dayMs),
                    category = "Event"
                ),
                AnnouncementEntity(
                    id = 3,
                    title = "Steam & Sauna Maintenance Scheduled",
                    message = "Routine deep sanitization and heater servicing for the steam room will take place this Sunday from 2 PM to 5 PM.",
                    date = now - (6 * dayMs),
                    category = "Notice"
                )
            )
            announcementDao.insertAll(initialAnnouncements)
        }
    }
}
