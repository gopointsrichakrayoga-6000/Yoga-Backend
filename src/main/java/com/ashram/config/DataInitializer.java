package com.ashram.config;

import com.ashram.entity.*;
import com.ashram.repository.CategoryRepository;
import com.ashram.repository.MediaItemRepository;
import com.ashram.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @org.springframework.beans.factory.annotation.Value("${app.seed-demo-data:false}")
    private boolean seedDemoData;

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository,
                                          CategoryRepository categoryRepository,
                                          MediaItemRepository mediaItemRepository,
                                          PasswordEncoder passwordEncoder) {
        return args -> {
            // =========================================================================================
            // WARNING / IMPORTANT SECURITY NOTE:
            // The seeded demo accounts (`admin@srichakrayoga.org` and `member@srichakrayoga.org`) below
            // are provided STRICTLY for local development, demonstration, and automated testing.
            // They ONLY run when SEED_DEMO_DATA=true (`app.seed-demo-data=true`) is set.
            // =========================================================================================
            User admin = null;
            if (seedDemoData) {
                if (!userRepository.existsByEmail("admin@srichakrayoga.org")) {
                    logger.warn("==================================================================================");
                    logger.warn("SEED_DEMO_DATA is TRUE — Seeding demo accounts (`admin@srichakrayoga.org` & `member@srichakrayoga.org`).");
                    logger.warn("Ensure SEED_DEMO_DATA=false before production deployment!");
                    logger.warn("==================================================================================");

                    admin = new User(
                            "Acharya (Admin)",
                            "admin@srichakrayoga.org",
                            passwordEncoder.encode("Password123!"),
                            Role.ADMIN
                    );
                    userRepository.save(admin);

                    User member = new User(
                            "Siddharth Sharma (Member)",
                            "member@srichakrayoga.org",
                            passwordEncoder.encode("Password123!"),
                            Role.USER
                    );
                    userRepository.save(member);
                } else {
                    admin = userRepository.findByEmail("admin@srichakrayoga.org").orElse(null);
                }
            } else {
                logger.info("Demo account seeding is DISABLED (SEED_DEMO_DATA=false). Skipping demo accounts.");
            }

            // Check if baseline seeding is needed or if leftover test/dev artifacts exist
            boolean needsSeeding = categoryRepository.count() == 0 || (seedDemoData && mediaItemRepository.findAll().stream().anyMatch(m ->
                    m.getTitle().toLowerCase().contains("screenshot") ||
                    m.getTitle().toLowerCase().contains("test") ||
                    m.getUrl().contains("1506126613408") ||
                    m.getUrl().contains("1507679799987") ||
                    m.getUrl().contains("1510812431401") ||
                    m.getUrl().contains("1545205597") ||
                    m.getUrl().contains("1609766418204")));

            if (needsSeeding) {
                if (categoryRepository.count() > 0) {
                    logger.info("Cleaning up old test artifacts and unverified media records before seeding pristine baseline...");
                    mediaItemRepository.deleteAll();
                    categoryRepository.deleteAll();
                }
                logger.info("Seeding initial ashram categories and baseline photographic/video archive...");
                Category practiceCat = new Category("Daily Shala Practice", "Dawn Surya Namaskar, traditional Ashtanga adjustments, and Pranayama sessions inside our teak mandapam.");
                Category groundsCat = new Category("Ashram Grounds & Architecture", "The Circular Arch Mandir, stone pathways, lotus ponds, and protected Nilgiri forest canopy.");
                Category discourseCat = new Category("Discourses & Satsang", "Evening philosophical talks, Upanishadic commentary, and guided stillness contemplation under the banyan tree.");
                Category festivalCat = new Category("Sacred Festivals", "Deepa Puja invocations, Guru Purnima gatherings, and traditional Vedic fire ceremonies.");

                categoryRepository.save(practiceCat);
                categoryRepository.save(groundsCat);
                categoryRepository.save(discourseCat);
                categoryRepository.save(festivalCat);

                // Seed Photos for Daily Shala Practice (8 photos so public preview shows first 6 + cutoff card)
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Surya Namaskar at Dawn", "Practitioners moving through classical solar sequences inside the open-air teak shala.", "https://upload.wikimedia.org/wikipedia/commons/2/29/Yoga_Teacher_Training_in_India.jpg", null, practiceCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Asana Structural Alignment", "Precise alignment instructions focused on spinal extension on natural stone floors.", "https://upload.wikimedia.org/wikipedia/commons/5/54/Early-morning-meditation-session-rishikesh-yogpeeth.jpg", null, practiceCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Pranayama & Nadi Shodhana", "Seated breath regulation practice cultivating pranic balance before morning stillness.", "https://upload.wikimedia.org/wikipedia/commons/0/03/Yoga_Padmasana_Lotus_Posture.jpg", null, practiceCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Padmasana Contemplation", "Silent sitting meditation on wooden mandapam platforms following vigorous Hatha discipline.", "https://upload.wikimedia.org/wikipedia/commons/d/d5/Figurine_of_a_Buddha_seated_in_a_lotus_position_on_a_throne%2C_Headless%2C_NUS_Museum_%28112041%29.jpg", null, practiceCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Morning Mantra Recitation", "Vedic invocation chanted collectively at 5:30 AM before sunrise beneath the canopy.", "https://upload.wikimedia.org/wikipedia/commons/c/c2/Satsang_Bihar_-_panoramio.jpg", null, practiceCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Evening Yin Restorative", "Slow, deep connective tissue release held by gentle brass diya candlelight.", "https://upload.wikimedia.org/wikipedia/commons/0/03/Yoga_Padmasana_Lotus_Posture.jpg", null, practiceCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Teak Shala Floor Preparation", "Sevak students cleaning and arranging natural cotton rugs before dawn practice.", "https://upload.wikimedia.org/wikipedia/commons/5/54/Early-morning-meditation-session-rishikesh-yogpeeth.jpg", null, practiceCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Stillness After Savasana", "The quiet resonance filling the stone-pillared hall at the conclusion of practice.", "https://upload.wikimedia.org/wikipedia/commons/2/29/Yoga_Teacher_Training_in_India.jpg", null, practiceCat, admin));

                // Seed Photos for Ashram Grounds (7 photos)
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "The Stone Pillar Mandapam", "Traditional South Indian stone and teak meditation hall designed with Vastu sacred geometry.", "https://images.unsplash.com/photo-1620766182966-c6eb5ed2b788?auto=format&fit=crop&w=1200&q=80", null, groundsCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Sanctuary Granite Gateway", "The entry stone portal framed by century-old banyan and Bodhi trees along the Nilgiri slopes.", "https://upload.wikimedia.org/wikipedia/commons/c/c4/Mamallapuram%2C_Mahabalipuram%2C_Shore_Temple%2C_India.jpg", null, groundsCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Sacred Lotus Courtyard Pond", "White and pink lotuses blooming alongside the stone temple garden path.", "https://upload.wikimedia.org/wikipedia/commons/d/d3/Nelumno_nucifera_open_flower_-_botanic_garden_adelaide2.jpg", null, groundsCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Ancient Temple Pillars", "Intricate stone carvings along the outer courtyard corridor where monks walk in meditation.", "https://upload.wikimedia.org/wikipedia/commons/4/4f/Meenakshi_Temple%2C_Pillars%2C_Madurai%2C_India.jpg", null, groundsCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Dhyana Kutir Stone Cells", "Individual contemplation granite cells reserved for silent retreat practitioners.", "https://upload.wikimedia.org/wikipedia/commons/a/a4/Side_corridor_of_Brihadisvara_Temple%2C_Thanjavur.jpg", null, groundsCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Monsoon Mist Over Nilgiri Foothills", "Early morning fog rolling down the mountain forest ridge above the ashram grounds.", "https://upload.wikimedia.org/wikipedia/commons/0/05/Shooting_point%2CNilgiri_hills%2Cooty.jpg", null, groundsCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Banyan Canopy Walkway", "The quiet stone path leading under ancient banyans to the evening satsang grove.", "https://images.unsplash.com/photo-1448375240586-882707db888b?auto=format&fit=crop&w=1200&q=80", null, groundsCat, admin));

                // Seed Photos for Discourses & Festivals (6 photos)
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Evening Satsang Discourse", "Our Acharya sharing commentary on the Mandukya Upanishad by brass oil diya lamp.", "https://upload.wikimedia.org/wikipedia/commons/8/8b/A_Deeya_Diya_oil_lamp_with_a_swastika_sign%2C_Hinduism_Varanasi_India.jpg", null, discourseCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Chanting the Peace Invocation", "Resident monks leading Vedic peace chants (`Om Sahana Bhavatu`) beneath the mandapam.", "https://upload.wikimedia.org/wikipedia/commons/c/cf/19th-century_Sukunda_ritual_lamp%2C_Newar_Nepal.jpg", null, discourseCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Deepa Puja on Full Moon", "108 traditional clay brass oil diyas lit across the stone courtyard during Kartik Purnima.", "https://upload.wikimedia.org/wikipedia/commons/1/19/19th_century_Deepa_Sundari%2C_Hyderabad_state_museum%2C_Telangana.jpg", null, festivalCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Sacred Havan Fire Invocation", "Vedic fire ceremony conducted with brass vessels to purify the atmosphere and offer gratitude.", "https://upload.wikimedia.org/wikipedia/commons/c/cf/19th-century_Sukunda_ritual_lamp%2C_Newar_Nepal.jpg", null, festivalCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Guru Purnima Silent Gathering", "Students offering marigold garlands and sitting in silent gratitude in the stone courtyard.", "https://upload.wikimedia.org/wikipedia/commons/4/4f/Meenakshi_Temple%2C_Pillars%2C_Madurai%2C_India.jpg", null, festivalCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.PHOTO, "Mandapam Flower Rangoli", "Intricate mandala designs arranged with marigold and jasmine petals at the shala entrance.", "https://upload.wikimedia.org/wikipedia/commons/d/d3/Nelumno_nucifera_open_flower_-_botanic_garden_adelaide2.jpg", null, festivalCat, admin));

                // Seed Videos (4 videos for Discourses so public preview shows first 2 + cutoff card)
                mediaItemRepository.save(new MediaItem(MediaItemType.VIDEO, "The Nature of Inner Stillness (Discourse #108)", "Recorded during the autumn retreat. Acharya explores how to observe thought currents without entanglement.", "jfKfPfyJRdk", "https://images.unsplash.com/photo-1620766182966-c6eb5ed2b788?auto=format&fit=crop&w=800&q=80", discourseCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.VIDEO, "Classical Surya Namaskar: Alignment & Breath", "Complete demonstration of the 12 solar postures with internal breath pacing (`Ujjayi`).", "s2NQhpFGIOg", "https://upload.wikimedia.org/wikipedia/commons/2/29/Yoga_Teacher_Training_in_India.jpg", practiceCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.VIDEO, "Morning Vedic Chanting: Purusha Suktam", "High-definition acoustic recording of sunrise chanting inside the stone pillar mandir.", "g_t4D5T_a4o", "https://upload.wikimedia.org/wikipedia/commons/4/4f/Meenakshi_Temple%2C_Pillars%2C_Madurai%2C_India.jpg", discourseCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.VIDEO, "Guided Yoga Nidra for Deep Restoration", "45-minute guided conscious relaxation recorded under the banyan grove.", "1ZYbU82GVz4", "https://images.unsplash.com/photo-1448375240586-882707db888b?auto=format&fit=crop&w=800&q=80", discourseCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.VIDEO, "A Walk Through the Sacred Grove Sanctuary", "Visual tour of the ashram stone architecture, ancient mandapam, and forest paths.", "v7AYKMP6rOE", "https://images.unsplash.com/photo-1620766182966-c6eb5ed2b788?auto=format&fit=crop&w=800&q=80", groundsCat, admin));
                mediaItemRepository.save(new MediaItem(MediaItemType.VIDEO, "Deepa Puja & Fire Ceremony Highlights", "Immersive footage from the annual full moon celebration with 108 brass oil lamps.", "8Z1eMy2FoX4", "https://upload.wikimedia.org/wikipedia/commons/8/8b/A_Deeya_Diya_oil_lamp_with_a_swastika_sign%2C_Hinduism_Varanasi_India.jpg", festivalCat, admin));

                logger.info("Successfully seeded baseline media database for Sri Chakra Yoga.");
            }
        };
    }
}
