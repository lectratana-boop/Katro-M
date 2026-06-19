package com.example.katro

object KatroStrings {
    private val fr = mapOf(
        "app_title" to "Katro Madagascar",
        "play" to "Jouer",
        "how_to_play" to "Comment jouer",
        "stats" to "Statistiques",
        "settings" to "Paramètres",
        "about" to "À propos",
        "resume_game" to "Reprendre la partie",
        "new_game_pvp" to "2 Joueurs (Passe & Joue)",
        "new_game_ai" to "Jouer contre l'IA",
        "score_label" to "Graines Capturées",
        "p1_label" to "Joueur 1",
        "p2_label" to "Joueur 2",
        "ai_label" to "IA",
        "active_turn" to "Tour du %s",
        "winner_is" to "Victoire de %s !",
        "draw_match" to "Match nul !",
        "game_over" to "Partie terminée !",
        "game_over_desc" to "Un joueur ne peut plus réaliser de coup valide.",
        "back_to_menu" to "Retour au menu",
        "cancel" to "Annuler",
        "ok" to "Valider",
        
        // Stats
        "stats_title" to "Statistiques locales",
        "stats_games" to "Parties jouées",
        "stats_wins_p1" to "Victoires Joueur 1",
        "stats_wins_p2" to "Victoires Joueur 2 / IA",
        "stats_draws" to "Matchs nuls",
        "stats_avg_time" to "Temps moyen de partie",
        "stats_max_cap" to "Plus grande capture en un tour",
        "stats_reset" to "Réinitialiser les statistiques",
        "stats_reset_confirm" to "Voulez-vous vraiment réinitialiser toutes les statistiques et badges ?",
        "stats_reset_done" to "Statistiques réinitialisées.",
        
        // Badges
        "badges_title" to "Récompenses & Badges",
        "badge_first_win" to "Première Victoire",
        "badge_first_win_desc" to "Gagner sa toute première partie",
        "badge_10_wins" to "Vétéran",
        "badge_10_wins_desc" to "Débloquer 10 victoires",
        "badge_50_wins" to "Maître du Katro",
        "badge_50_wins_desc" to "Devenir un grand champion avec 50 victoires",
        "badge_legend_cap" to "Capture Légendaire",
        "badge_legend_cap_desc" to "Capturer 6 graines ou plus en un seul tour !",
        "badge_loyal_player" to "Joueur Fidèle",
        "badge_loyal_player_desc" to "Jouer 15 parties au total",
        
        // Settings
        "settings_title" to "Paramètres",
        "settings_sound" to "Effets sonores",
        "settings_fast_anim" to "Animations rapides",
        "settings_rotate_p2" to "Rotation automatique (Mode 2J)",
        "settings_lang" to "Langue de l'application",
        "settings_ai_diff" to "Difficulté de l'IA",
        "diff_easy" to "Débutant (Coups aléatoires)",
        "diff_medium" to "Intermédiaire (Privilégie les captures)",
        "diff_hard" to "Expert (Anticipation de coups)",
        "settings_saved" to "Paramètres sauvegardés automatiquement.",
        
        // About
        "about_title" to "À propos",
        "about_p1" to "Katro Madagascar est un hommage moderne à l'un des trésors culturels et intellectuels les plus précieux de la Grande Île.",
        "about_p2" to "Inspiré de la grande famille des jeux de semailles ou Mancala, le Katro exige de la tactique, de l'anticipation et de la vivacité d'esprit.",
        "about_p3" to "Cette version a été développée pour fonctionner entièrement hors ligne, consommer très peu d'énergie et être fluide sur tous les appareils Android pour que petits et grands puissent jouer n'importe où, n'importe quand.",
        "about_p4" to "Préservez et transmettez le patrimoine malgache. Que le meilleur gagne !",
        
        // Tutorial Screen
        "tuto_title" to "Comment jouer",
        "tuto_slide_1_title" to "Le Plateau",
        "tuto_slide_1" to "Le jeu de Katro se compose de 2 rangées de 8 trous (16 trous au total). Chaque joueur possède l'une des rangées de 8 trous en face de lui. Au début, chaque trou contient exactement 2 graines (32 graines au total).",
        "tuto_slide_2_title" to "Comment Distribuer",
        "tuto_slide_2" to "À votre tour, choisissez un trou de votre rangée qui contient au moins 2 graines. Prenez toutes les graines et distribuez-les une par une dans les trous suivants, dans le sens inverse des aiguilles d'une montre (sens antihoraire). Elle s'étend aussi chez votre adversaire en boucle !",
        "tuto_slide_3_title" to "Le Relais",
        "tuto_slide_3" to "Si la dernière graine tombe dans un trou contenant déjà d'autres graines, vous reprenez TOUTES les graines de ce trou et continuez à semer dans le même sens. Le tour continue tant que la dernière graine ne tombe pas dans un trou vide !",
        "tuto_slide_4_title" to "La Capture et Victoire",
        "tuto_slide_4" to "Si votre dernière graine atterrit dans l'un de vos trous qui était vide, et que le trou opposé de l'adversaire contient des graines, vous les CAPTUREZ toutes ! Elles s'ajoutent à votre score. La partie se termine quand un joueur ne peut plus jouer faute de graines suffisantes. Le meilleur score l'emporte !",
        "next" to "Suivant",
        "prev" to "Précédent",
        "got_it" to "Compris !",
        "seconds_unit" to "s"
    )

    private val mg = mapOf(
        "app_title" to "Katro Madagascar",
        "play" to "Hilalao",
        "how_to_play" to "Fomba filalao",
        "stats" to "Antontan'isa",
        "settings" to "Fikirakirana",
        "about" to "Mombamomba",
        "resume_game" to "Hanohy ny lalao teo aloha",
        "new_game_pvp" to "Mpilalao 2 (Mifandimby)",
        "new_game_ai" to "Milalao amin'ny Solo (IA)",
        "score_label" to "Voa azonao tsirairay",
        "p1_label" to "Mpilalao 1",
        "p2_label" to "Mpilalao 2",
        "ai_label" to "IA",
        "active_turn" to "Anjaran'i %s ankehitriny",
        "winner_is" to "Nandresy i %s !",
        "draw_match" to "Sahala ny lalao !",
        "game_over" to "Tapitra ny lalao !",
        "game_over_desc" to "Tsy misy hetsika azo atao intsony izao.",
        "back_to_menu" to "Hiverina amin'ny fandraisana",
        "cancel" to "Hanafoana",
        "ok" to "Ekena",
        
        // Stats
        "stats_title" to "Ireo antontan'isa eo an-toerana",
        "stats_games" to "Lalao efa vita",
        "stats_wins_p1" to "Fandresen'ny Mpilalao 1",
        "stats_wins_p2" to "Fandresen'ny Mpilalao 2 / IA",
        "stats_draws" to "Lalao sahala",
        "stats_avg_time" to "Fe-potoana lalao iray",
        "stats_max_cap" to "Fisamborana lehibe indrindra",
        "stats_reset" to "Hamafa ny antontan'isa rehetra",
        "stats_reset_confirm" to "Hamafa tanteraka ny antontan'isa sy ireo mari-pandresena rehetra ve ianao?",
        "stats_reset_done" to "Voafafa avokoa ny antontan'isa.",
        
        // Badges
        "badges_title" to "Ireo mari-pahombiazana azo",
        "badge_first_win" to "Fandresena voalohany",
        "badge_first_win_desc" to "Nahazo ny fandresena voalohany teo amin'ny tantara",
        "badge_10_wins" to "Mpilalao mahay",
        "badge_10_wins_desc" to "Nahazo fandresena miisa 10",
        "badge_50_wins" to "Tompon-dakan'ny Katro",
        "badge_50_wins_desc" to "Tena mahay amin'ny fandresena miisa 50",
        "badge_legend_cap" to "Sambotra Ambara",
        "badge_legend_cap_desc" to "Nisambotra voa miisa 6 na mihoatra tao anatin'ny indray mihodina !",
        "badge_loyal_player" to "Mpilalao tsy mivadika",
        "badge_loyal_player_desc" to "Nilalao in-15 mitsinjara tsara",
        
        // Settings
        "settings_title" to "Fikirakirana ny lalao",
        "settings_sound" to "Feo rehetra",
        "settings_fast_anim" to "Sarimihetsika haingana",
        "settings_rotate_p2" to "Hahodina ny solaitra (Mpilalao 2)",
        "settings_lang" to "Fitenin'ny fampiharana",
        "settings_ai_diff" to "Haavon'ny Solo (IA)",
        "diff_easy" to "Mbola mianatra (Kisendrasendra)",
        "diff_medium" to "Mahay (Tia misambotra)",
        "diff_hard" to "Katry (Mandinika alohan'ny hilalao)",
        "settings_saved" to "Voatahiry avy hatrany ny fikirakirana.",
        
        // About
        "about_title" to "Mombamomba",
        "about_p1" to "Ny Katro Madagascar dia fanomezam-boninahitra ny lalao nentim-paharazana malagasy sady soatoavina ara-tsain'ny Nosintsika.",
        "about_p2" to "Tahaka ny vatan'ny lalao Mancala dia mitaky fahamalinana, paikady maty paika, ary fisainana haingana ny Katro.",
        "about_p3" to "Izao fampiharana izao dia natao hiasa any ivelan'ny aterineto (offline), nefa tsy handany hery ary mandeha tsara amin'ny finday rehetra mba ahafahan'ny antitra sy ny tanora milalao malalaka na aiza na aiza.",
        "about_p4" to "Andao hovelomina sy hampita ny kolontsaina malagasy. Izay mahay no mandresy !",
        
        // Tutorial Screen
        "tuto_title" to "Fomba filalaovana",
        "tuto_slide_1_title" to "Ny Solaitra",
        "tuto_slide_1" to "Ny lalao Katro dia misy lavaka 16 mizara dika 2 misy lavaka 8 avy. Ny mpilalao tsirairay dia manana ny dika 8 manoloana azy. Eo am-piandohana dia misy voa 2 isaky ny lavaka, izany hoe 32 ny fitambarany.",
        "tuto_slide_2_title" to "Fizarana voa",
        "tuto_slide_2" to "Anjaranao izao, misafidy lavaka manana voa 2 farafahakeliny eo amin'ny dika anao ianao. Raisinao ny voa rehetra ao ary zarainao iray isaky ny lavaka manaraka, manaraka ny fihodinana mifanohitra amin'ny famantaranandro (anti-horaire). Afaka miampita any amin'ny mpifanandrina koa izany.",
        "tuto_slide_3_title" to "Ny Relais",
        "tuto_slide_3" to "Raha latsaka amin'ny lavaka efa nisy voa teo aloha ny voa farany teny an-tananao, dia raisinao ny voa rehetra ao amin'io lavaka io ka tohizanao indray ny fizarana. Tsy mijanona izany raha tsy latsaka amin'ny lavaka foana ny voa farany.",
        "tuto_slide_4_title" to "Fisamborana sy Fandresena",
        "tuto_slide_4" to "Raha latsaka amin'ny lavaka foana anao ny voa farany, ary misy voa ny lavaka tandrifiny manoloana any amin'ny mpifanandrina, dia azonao samborina ny voany rehetra ao ! Ampiana amin'ny isanao izany. Tapitra ny lalao rehefa tsy misy mpilalao afaka manao hetsika intsony. Izay manana voa betsaka no mpandresy !",
        "next" to "Manaraka",
        "prev" to "Teo aloha",
        "got_it" to "Efa azoko !",
        "seconds_unit" to "s"
    )

    fun get(key: String, lang: String): String {
        val dict = if (lang == "mg") mg else fr
        return dict[key] ?: (fr[key] ?: key)
    }

    /**
     * Helper to format string template parameters (similar to String.format).
     */
    fun getFormatted(key: String, lang: String, param: String): String {
        val template = get(key, lang)
        return template.replace("%s", param)
    }
}
