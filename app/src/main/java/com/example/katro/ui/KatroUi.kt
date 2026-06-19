package com.example.katro.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.katro.*
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Main entrance view switcher.
 */
@Composable
fun KatroMainLayout(viewModel: KatroViewModel) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Earthy subtle tribal visual borders
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        // Drawing traditional Madagascar zigzag border accents dynamically
                        val stripeWidth = 10.dp.toPx()
                        val path = Path()
                        var x = 0f
                        var isUp = true
                        path.moveTo(0f, 0f)
                        while (x < size.width) {
                            val nextX = x + stripeWidth
                            val nextY = if (isUp) stripeWidth else 0f
                            path.lineTo(nextX, nextY)
                            x = nextX
                            isUp = !isUp
                        }
                        path.lineTo(size.width, 0f)
                        path.close()
                        drawPath(path, color = WoodBark.copy(alpha = 0.4f))
                    }
            ) {
                when (viewModel.currentScreen) {
                    KatroScreen.HOME -> HomeScreen(viewModel)
                    KatroScreen.GAME -> GameScreen(viewModel)
                    KatroScreen.HOW_TO_PLAY -> HowToPlayScreen(viewModel)
                    KatroScreen.STATS -> StatsAndBadgesScreen(viewModel)
                    KatroScreen.SETTINGS -> SettingsScreen(viewModel)
                    KatroScreen.ABOUT -> AboutScreen(viewModel)
                }
            }
        }
    }
}

/**
 * HomeScreen with wood texture design, logo banner and menu buttons.
 */
@Composable
fun HomeScreen(viewModel: KatroViewModel) {
    val lang = viewModel.language

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // App Logo & Decorative Title
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "KATRO",
                fontSize = 54.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif,
                color = AmberGold,
                modifier = Modifier.testTag("home_title")
            )
            Text(
                text = "MADAGASCAR",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp,
                color = WoodAsh
            )
        }

        // Image banner with round border representing wood board
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(160.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WoodDeepBrown)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.img_katro_hero),
                    contentDescription = "Madagascar Katro Board Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Add soft wooden gradient highlight overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, WoodDarkBackground.copy(alpha = 0.8f))
                            )
                        )
                )
                // Small Malagasy traditional phrase
                Text(
                    text = "Lalao nentim-paharazana",
                    color = WoodAsh,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                )
            }
        }

        // Action menu options list
        Column(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Resume option if save exists
            if (viewModel.hasOngoingGame()) {
                Button(
                    onClick = { viewModel.resumeExistingGame() },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberCore, contentColor = WoodDarkBackground),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("resume_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = KatroStrings.get("resume_game", lang),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Play against Friend Local Pass & Play
            Button(
                onClick = { viewModel.startNewGame(vsAI = false) },
                colors = ButtonDefaults.buttonColors(containerColor = WoodSatin, contentColor = CustomWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("play_pvp_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.AccountBox, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(KatroStrings.get("new_game_pvp", lang), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            // Play against computer (AI match)
            Button(
                onClick = { viewModel.startNewGame(vsAI = true) },
                colors = ButtonDefaults.buttonColors(containerColor = WoodBark, contentColor = CustomWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("play_ai_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(KatroStrings.get("new_game_ai", lang), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            // Dual row grid for subsidiary options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tutorial
                Button(
                    onClick = { viewModel.currentScreen = KatroScreen.HOW_TO_PLAY },
                    colors = ButtonDefaults.buttonColors(containerColor = WoodWarmCaramel, contentColor = WoodAsh),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("tuto_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(KatroStrings.get("how_to_play", lang), fontSize = 12.sp, maxLines = 1)
                }

                // Stats
                Button(
                    onClick = { viewModel.currentScreen = KatroScreen.STATS },
                    colors = ButtonDefaults.buttonColors(containerColor = WoodWarmCaramel, contentColor = WoodAsh),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("stats_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(KatroStrings.get("stats", lang), fontSize = 12.sp, maxLines = 1)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Settings
                Button(
                    onClick = { viewModel.currentScreen = KatroScreen.SETTINGS },
                    colors = ButtonDefaults.buttonColors(containerColor = WoodWarmCaramel, contentColor = WoodAsh),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("settings_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(KatroStrings.get("settings", lang), fontSize = 12.sp, maxLines = 1)
                }

                // About
                Button(
                    onClick = { viewModel.currentScreen = KatroScreen.ABOUT },
                    colors = ButtonDefaults.buttonColors(containerColor = WoodWarmCaramel, contentColor = WoodAsh),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("about_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(KatroStrings.get("about", lang), fontSize = 12.sp, maxLines = 1)
                }
            }
        }

        // Language toggle at bottom
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(WoodDeepBrown)
                .padding(4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (lang == "fr") WoodSatin else Color.Transparent)
                    .clickable { viewModel.changeLanguage("fr") }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("FR", color = if (lang == "fr") CustomWhite else WoodAsh, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (lang == "mg") WoodSatin else Color.Transparent)
                    .clickable { viewModel.changeLanguage("mg") }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("MG", color = if (lang == "mg") CustomWhite else WoodAsh, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * GameScreen rendering board, scores, turns and rotation.
 */
@Composable
fun GameScreen(viewModel: KatroViewModel) {
    val lang = viewModel.language
    val isP1Active = viewModel.gameState.activePlayer == Player.PLAYER_ONE
    val activeLabel = if (isP1Active) {
        KatroStrings.get("p1_label", lang)
    } else if (viewModel.isAiMode) {
        KatroStrings.get("ai_label", lang)
    } else {
        KatroStrings.get("p2_label", lang)
    }

    // Determine current board rotation (180 deg for P2 if active and enabled)
    val shouldRotate = !isP1Active && !viewModel.isAiMode && viewModel.rotateBoardForP2
    val boardRotation by animateFloatAsState(
        targetValue = if (shouldRotate) 180f else 0f,
        animationSpec = tween(durationMillis = 600)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper Header Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.currentScreen = KatroScreen.HOME },
                modifier = Modifier.testTag("game_back_button")
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Menu", tint = WoodAsh)
            }

            // Top Status badge indication
            Card(
                colors = CardDefaults.cardColors(containerColor = WoodWarmCaramel),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (viewModel.isAnimating) AmberGold else Color.Green)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (viewModel.isAnimating) "Sowing..." else "Offline",
                        color = WoodAsh,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(
                onClick = { viewModel.startNewGame(viewModel.isAiMode) },
                modifier = Modifier.testTag("game_restart_button")
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset match", tint = WoodAsh)
            }
        }

        // Scores & Active Player Display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player 1 Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = if (isP1Active) WoodSatin else WoodDeepBrown
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = KatroStrings.get("p1_label", lang),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isP1Active) CustomWhite else CustomGrey
                    )
                    Text(
                        text = "${viewModel.gameState.scoreP1}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                    Text(
                        text = KatroStrings.get("score_label", lang),
                        fontSize = 9.sp,
                        color = WoodAsh
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Player 2 / AI Card
            val p2Color = if (!isP1Active) WoodSatin else WoodDeepBrown
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = p2Color),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (viewModel.isAiMode) KatroStrings.get("ai_label", lang) else KatroStrings.get("p2_label", lang),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (!isP1Active) CustomWhite else CustomGrey
                    )
                    Text(
                        text = "${viewModel.gameState.scoreP2}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                    Text(
                        text = KatroStrings.get("score_label", lang),
                        fontSize = 9.sp,
                        color = WoodAsh
                    )
                }
            }
        }

        // Turn indicator label
        Card(
            colors = CardDefaults.cardColors(containerColor = WoodDeepBrown.copy(alpha = 0.5f)),
            modifier = Modifier.padding(vertical = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = KatroStrings.getFormatted("active_turn", lang, activeLabel),
                color = AmberGold,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("active_turn_text")
            )
        }

        // Board Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Recreating the exact Malagasy folding board from the user's photo with a Left slab and a Right slab
            Row(
                modifier = Modifier
                    .graphicsLayer(rotationZ = boardRotation)
                    .fillMaxWidth()
                    .aspectRatio(1.25f)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // LEFT WOODEN SLAB (Columns 1-4)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .shadow(8.dp, RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp, topEnd = 4.dp, bottomEnd = 4.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(NaturalBoardBase, NaturalBoardBorder)
                            )
                        )
                        .border(3.2.dp, NaturalBoardBorder, RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp, topEnd = 4.dp, bottomEnd = 4.dp))
                        .padding(vertical = 10.dp, horizontal = 6.dp),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Left Top Row Holes (Indices: 15, 14, 13, 12)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val leftP2Holes = listOf(15, 14, 13, 12)
                        leftP2Holes.forEach { idx ->
                            HoleView(
                                index = idx,
                                count = viewModel.currentUiBoard[idx],
                                isHighlighted = viewModel.activeAnimationHoleIdx == idx,
                                isPlayable = !viewModel.isAnimating && !viewModel.gameState.isGameOver && !isP1Active && !viewModel.isAiMode,
                                onHoleClicked = { viewModel.performMove(idx) }
                            )
                        }
                    }

                    // Traditional zigzag/tribal linear design in left board
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                    ) {
                        val midY = size.height / 2
                        val path = Path()
                        path.moveTo(0f, midY)
                        var tx = 0f
                        val step = 15f
                        var up = true
                        while (tx < size.width) {
                            path.lineTo(tx, if (up) midY - 3f else midY + 3f)
                            tx += step
                            up = !up
                        }
                        drawPath(path, color = NaturalBoardBorder.copy(alpha = 0.8f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
                    }

                    // Left Bottom Row Holes (Indices: 0, 1, 2, 3)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val leftP1Holes = listOf(0, 1, 2, 3)
                        leftP1Holes.forEach { idx ->
                            HoleView(
                                index = idx,
                                count = viewModel.currentUiBoard[idx],
                                isHighlighted = viewModel.activeAnimationHoleIdx == idx,
                                isPlayable = !viewModel.isAnimating && !viewModel.gameState.isGameOver && isP1Active,
                                onHoleClicked = { viewModel.performMove(idx) }
                            )
                        }
                    }
                }

                // CENTRAL BRASS HINGES & FOLDING SPLIT JOINT (Exactly like the photo!)
                Column(
                    modifier = Modifier
                        .width(16.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Hinge Joint
                    Box(
                        modifier = Modifier
                            .width(10.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF8B7355), Color(0xFFCDAF95), Color(0xFF5C4033))
                                )
                            )
                            .border(1.dp, Color(0xFF321A10), RoundedCornerShape(2.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(modifier = Modifier.size(2.dp).clip(CircleShape).background(Color(0xFF150A05)))
                            Box(modifier = Modifier.size(2.dp).clip(CircleShape).background(Color(0xFF150A05)))
                        }
                    }

                    // Central split line shadow accent
                    Box(
                        modifier = Modifier
                            .width(2.2.dp)
                            .weight(1f)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF150A05), Color(0xFF24140D), Color(0xFF150A05))
                                )
                            )
                    )

                    // Bottom Hinge Joint
                    Box(
                        modifier = Modifier
                            .width(10.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF8B7355), Color(0xFFCDAF95), Color(0xFF5C4033))
                                )
                            )
                            .border(1.dp, Color(0xFF321A10), RoundedCornerShape(2.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(modifier = Modifier.size(2.dp).clip(CircleShape).background(Color(0xFF150A05)))
                            Box(modifier = Modifier.size(2.dp).clip(CircleShape).background(Color(0xFF150A05)))
                        }
                    }
                }

                // RIGHT WOODEN SLAB (Columns 5-8)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .shadow(8.dp, RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 24.dp, bottomEnd = 24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(NaturalBoardBase, NaturalBoardBorder)
                            )
                        )
                        .border(3.2.dp, NaturalBoardBorder, RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 24.dp, bottomEnd = 24.dp))
                        .padding(vertical = 10.dp, horizontal = 6.dp),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Right Top Row Holes (Indices: 11, 10, 9, 8)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val rightP2Holes = listOf(11, 10, 9, 8)
                        rightP2Holes.forEach { idx ->
                            HoleView(
                                index = idx,
                                count = viewModel.currentUiBoard[idx],
                                isHighlighted = viewModel.activeAnimationHoleIdx == idx,
                                isPlayable = !viewModel.isAnimating && !viewModel.gameState.isGameOver && !isP1Active && !viewModel.isAiMode,
                                onHoleClicked = { viewModel.performMove(idx) }
                            )
                        }
                    }

                    // Traditional zigzag/tribal linear design in right board
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                    ) {
                        val midY = size.height / 2
                        val path = Path()
                        path.moveTo(0f, midY)
                        var tx = 0f
                        val step = 15f
                        var up = true
                        while (tx < size.width) {
                            path.lineTo(tx, if (up) midY - 3f else midY + 3f)
                            tx += step
                            up = !up
                        }
                        drawPath(path, color = NaturalBoardBorder.copy(alpha = 0.8f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
                    }

                    // Right Bottom Row Holes (Indices: 4, 5, 6, 7)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val rightP1Holes = listOf(4, 5, 6, 7)
                        rightP1Holes.forEach { idx ->
                            HoleView(
                                index = idx,
                                count = viewModel.currentUiBoard[idx],
                                isHighlighted = viewModel.activeAnimationHoleIdx == idx,
                                isPlayable = !viewModel.isAnimating && !viewModel.gameState.isGameOver && isP1Active,
                                onHoleClicked = { viewModel.performMove(idx) }
                            )
                        }
                    }
                }
            }

            // Floater indicator showing moving seeds held in hand during animation
            if (viewModel.isAnimating && viewModel.animationHeldSeedsCount > 0) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AmberCore),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .shadow(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = WoodDarkBackground, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${viewModel.animationHeldSeedsCount}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = WoodDarkBackground
                        )
                    }
                }
            }
        }

        // Help info hint
        Text(
            text = if (isP1Active) "P1: Jouez sur la rangée du bas" else if (viewModel.isAiMode) "L'IA réfléchit..." else "P2: Jouez sur la rangée du haut (tournée)",
            fontSize = 11.sp,
            color = WoodAsh.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }

    // Modal GameOver Dialog
    if (viewModel.gameState.isGameOver) {
        val winnerText = if (viewModel.gameState.isDraw) {
            KatroStrings.get("draw_match", lang)
        } else {
            val name = if (viewModel.gameState.winner == Player.PLAYER_ONE) {
                KatroStrings.get("p1_label", lang)
            } else if (viewModel.isAiMode) {
                KatroStrings.get("ai_label", lang)
            } else {
                KatroStrings.get("p2_label", lang)
            }
            KatroStrings.getFormatted("winner_is", lang, name)
        }

        AlertDialog(
            onDismissRequest = {},
            containerColor = WoodDeepBrown,
            title = {
                Text(
                    text = KatroStrings.get("game_over", lang),
                    color = AmberGold,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
            },
            text = {
                Column {
                    Text(text = winnerText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CustomWhite)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = KatroStrings.get("game_over_desc", lang),
                        color = WoodAsh,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.currentScreen = KatroScreen.HOME },
                    colors = ButtonDefaults.buttonColors(containerColor = WoodSatin)
                ) {
                    Text(KatroStrings.get("back_to_menu", lang))
                }
            }
        )
    }
}

/**
 * Custom-drawn representation of Katro Board Hole.
 */
@Composable
fun HoleView(
    index: Int,
    count: Int,
    isHighlighted: Boolean,
    isPlayable: Boolean,
    onHoleClicked: () -> Unit
) {
    // Halo glow factor
    val borderStroke = if (isHighlighted) {
        Modifier.border(2.dp, AmberCore, CircleShape)
    } else if (isPlayable && count >= 2) {
        Modifier.border(1.dp, NaturalHoleBorder.copy(alpha = 0.6f), CircleShape)
    } else {
        Modifier.border(1.dp, NaturalHoleBorder.copy(alpha = 0.3f), CircleShape)
    }

    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(NaturalHoleBg, NaturalBoardBase)
                )
            )
            .then(borderStroke)
            .clickable(enabled = isPlayable && count >= 2) { onHoleClicked() }
            .testTag("hole_$index"),
        contentAlignment = Alignment.Center
    ) {
        // Render pebbles / seeds canvas dynamically
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2

            // Draw shadow ring representing carved depth
            drawCircle(
                color = Color.Black.copy(alpha = 0.4f),
                radius = radius - 1.dp.toPx(),
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx())
            )

            // Draw individual organic seeds
            // Drawing up to 5 visual seeds. Overlapping.
            val seedVisualsCount = count.coerceAtMost(5)
            val baseColors = listOf(SeedIvory, SeedEarth, SeedPebble)
            
            // Fixed pseudo-random position seed offsets
            for (i in 0 until seedVisualsCount) {
                val seedColor = baseColors[i % baseColors.size]
                val angle = (i * 72 + (index * 13)) * Math.PI / 180.0
                val distance = 6.dp.toPx()
                val sx = center.x + (distance * cos(angle)).toFloat()
                val sy = center.y + (distance * sin(angle)).toFloat()

                // Draw organic pebble ellipse
                drawOval(
                    color = seedColor,
                    topLeft = Offset(sx - 3.5f.dp.toPx(), sy - 2.5f.dp.toPx()),
                    size = Size(7.dp.toPx(), 5.dp.toPx())
                )
            }
        }

        // Show numeric count overlay for quick scanning
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-2).dp, y = (-2).dp)
                .clip(CircleShape)
                .background(if (isHighlighted) AmberCore else WoodWarmCaramel.copy(alpha = 0.85f))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$count",
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                color = if (isHighlighted) WoodDarkBackground else CustomWhite,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Multilingual slide tutorial view.
 */
@Composable
fun HowToPlayScreen(viewModel: KatroViewModel) {
    val lang = viewModel.language
    var stepIndex by remember { mutableStateOf(0) }

    val slides = listOf(
        Pair("tuto_slide_1_title", "tuto_slide_1"),
        Pair("tuto_slide_2_title", "tuto_slide_2"),
        Pair("tuto_slide_3_title", "tuto_slide_3"),
        Pair("tuto_slide_4_title", "tuto_slide_4")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = KatroStrings.get("tuto_title", lang),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AmberGold,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "${stepIndex + 1} / ${slides.size}",
                fontSize = 14.sp,
                color = WoodAsh,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Illustrated Slide representation Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = WoodDeepBrown),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, WoodBark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Little Interactive / Illustrated schematic rendering
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(WoodDarkBackground)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (stepIndex) {
                        0 -> {
                            // Drawing 2 rows of 4 holes as schematic
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                repeat(4) {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(WoodBark), contentAlignment = Alignment.Center){
                                            Text("2", color = CustomWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(WoodWarmCaramel), contentAlignment = Alignment.Center){
                                            Text("2", color = CustomWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                        1 -> {
                            // Sowing diagram: drawing curve hand
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = AmberCore, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Pick up → Drop 1, 1, 1...", color = WoodAsh, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        2 -> {
                            // Relais diagram: Landing on index and picking up
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(AmberGold), contentAlignment = Alignment.Center) {
                                        Text("+1", color = WoodDarkBackground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Landed on 3 seeds!", color = WoodAsh, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = AmberCore, modifier = Modifier.size(20.dp))
                                Text("New sowing turn starts!", color = AmberCore, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        3 -> {
                            // Capture diagram
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("OPPONENT", color = WoodAsh, fontSize = 9.sp)
                                    Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(WoodBark), contentAlignment = Alignment.Center) {
                                        Text("4", color = CustomWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AmberCore, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("YOU (EMPTY)", color = WoodAsh, fontSize = 9.sp)
                                    Box(modifier = Modifier.size(24.dp).clip(CircleShape).border(1.dp, AmberCore, CircleShape), contentAlignment = Alignment.Center) {
                                        Text("1", color = AmberCore, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Text Description
                Text(
                    text = KatroStrings.get(slides[stepIndex].first, lang),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = KatroStrings.get(slides[stepIndex].second, lang),
                    fontSize = 14.sp,
                    color = WoodAsh,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.weight(1f).padding(top = 6.dp)
                )
            }
        }

        // Action controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (stepIndex > 0) stepIndex--
                },
                enabled = stepIndex > 0,
                colors = ButtonDefaults.buttonColors(containerColor = WoodWarmCaramel, disabledContainerColor = WoodWarmCaramel.copy(alpha = 0.3f))
            ) {
                Text(KatroStrings.get("prev", lang))
            }

            if (stepIndex < slides.size - 1) {
                Button(
                    onClick = { stepIndex++ },
                    colors = ButtonDefaults.buttonColors(containerColor = WoodSatin)
                ) {
                    Text(KatroStrings.get("next", lang))
                }
            } else {
                Button(
                    onClick = { viewModel.currentScreen = KatroScreen.HOME },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberCore, contentColor = WoodDarkBackground)
                ) {
                    Text(KatroStrings.get("got_it", lang))
                }
            }
        }
    }
}

/**
 * Screen showing statistics counters and unlocked achievement Badges.
 */
@Composable
fun StatsAndBadgesScreen(viewModel: KatroViewModel) {
    val lang = viewModel.language
    val context = LocalContext.current
    var showResetConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.currentScreen = KatroScreen.HOME }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Menu", tint = WoodAsh)
            }
            Text(
                text = KatroStrings.get("stats_title", lang),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = AmberGold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WoodDeepBrown)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        RowValue(KatroStrings.get("stats_games", lang), "${viewModel.statsGamesPlayed}")
                        Divider(color = WoodBark, modifier = Modifier.padding(vertical = 8.dp))
                        RowValue(KatroStrings.get("stats_wins_p1", lang), "${viewModel.statsWinsP1}")
                        Divider(color = WoodBark, modifier = Modifier.padding(vertical = 8.dp))
                        RowValue(KatroStrings.get("stats_wins_p2", lang), "${viewModel.statsWinsP2}")
                        Divider(color = WoodBark, modifier = Modifier.padding(vertical = 8.dp))
                        RowValue(KatroStrings.get("stats_draws", lang), "${viewModel.statsDraws}")
                        Divider(color = WoodBark, modifier = Modifier.padding(vertical = 8.dp))
                        RowValue(KatroStrings.get("stats_avg_time", lang), "${viewModel.statsAverageTimeSec}${KatroStrings.get("seconds_unit", lang)}")
                        Divider(color = WoodBark, modifier = Modifier.padding(vertical = 8.dp))
                        RowValue(KatroStrings.get("stats_max_cap", lang), "${viewModel.statsMaxCaptureSingleTurn}")
                    }
                }
            }

            // Badges section header
            item {
                Text(
                    text = KatroStrings.get("badges_title", lang),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 5 badges rendering
            item {
                BadgeCard(
                    title = KatroStrings.get("badge_first_win", lang),
                    desc = KatroStrings.get("badge_first_win_desc", lang),
                    isUnlocked = viewModel.badgeFirstWin,
                    icon = Icons.Default.Star
                )
            }
            item {
                BadgeCard(
                    title = KatroStrings.get("badge_10_wins", lang),
                    desc = KatroStrings.get("badge_10_wins_desc", lang),
                    isUnlocked = viewModel.badgeTenWins,
                    icon = Icons.Default.ThumbUp
                )
            }
            item {
                BadgeCard(
                    title = KatroStrings.get("badge_50_wins", lang),
                    desc = KatroStrings.get("badge_50_wins_desc", lang),
                    isUnlocked = viewModel.badgeFiftyWins,
                    icon = Icons.Default.Face
                )
            }
            item {
                BadgeCard(
                    title = KatroStrings.get("badge_legend_cap", lang),
                    desc = KatroStrings.get("badge_legend_cap_desc", lang),
                    isUnlocked = viewModel.badgeLegendaryCapture,
                    icon = Icons.Default.Favorite
                )
            }
            item {
                BadgeCard(
                    title = KatroStrings.get("badge_loyal_player", lang),
                    desc = KatroStrings.get("badge_loyal_player_desc", lang),
                    isUnlocked = viewModel.badgeLoyalPlayer,
                    icon = Icons.Default.Person
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { showResetConfirm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("reset_stats_button")
                ) {
                    Text(KatroStrings.get("stats_reset", lang))
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            containerColor = WoodDeepBrown,
            title = { Text(KatroStrings.get("stats_reset", lang), color = AmberGold, fontWeight = FontWeight.Bold) },
            text = { Text(KatroStrings.get("stats_reset_confirm", lang), color = WoodAsh) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetStatistics()
                        showResetConfirm = false
                    }
                ) {
                    Text(KatroStrings.get("ok", lang), color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text(KatroStrings.get("cancel", lang), color = WoodAsh)
                }
            }
        )
    }
}

@Composable
fun RowValue(label: String, valStr: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = WoodAsh, fontSize = 14.sp)
        Text(text = valStr, color = CustomWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BadgeCard(
    title: String,
    desc: String,
    isUnlocked: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val opacity = if (isUnlocked) 1.0f else 0.4f
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(alpha = opacity),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) WoodWarmCaramel else WoodDeepBrown
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isUnlocked) BorderStroke(1.5.dp, AmberCore) else BorderStroke(1.dp, WoodBark.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isUnlocked) AmberCore else WoodBark)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isUnlocked) WoodDarkBackground else CustomWhite
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (isUnlocked) AmberGold else CustomWhite)
                Text(text = desc, fontSize = 11.sp, color = WoodAsh)
            }

            if (!isUnlocked) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Locked", tint = WoodAsh, modifier = Modifier.size(16.dp))
            } else {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Unlocked", tint = AmberGold)
            }
        }
    }
}

/**
 * Settings configuration sliders and switches.
 */
@Composable
fun SettingsScreen(viewModel: KatroViewModel) {
    val lang = viewModel.language

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.currentScreen = KatroScreen.HOME }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Menu", tint = WoodAsh)
            }
            Text(
                text = KatroStrings.get("settings_title", lang),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = AmberGold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Audio switch
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WoodDeepBrown)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(KatroStrings.get("settings_sound", lang), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CustomWhite)
                        }
                        Switch(
                            checked = viewModel.soundEnabled,
                            onCheckedChange = { viewModel.toggleSounds() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AmberCore,
                                checkedTrackColor = WoodBark
                            ),
                            modifier = Modifier.testTag("sound_switch")
                        )
                    }
                }
            }

            // Speed animations switch
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WoodDeepBrown)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(KatroStrings.get("settings_fast_anim", lang), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CustomWhite)
                        }
                        Switch(
                            checked = viewModel.fastAnimations,
                            onCheckedChange = { viewModel.toggleFastAnimations() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AmberCore,
                                checkedTrackColor = WoodBark
                            )
                        )
                    }
                }
            }

            // Rotate Board pass and play switch
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WoodDeepBrown)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(KatroStrings.get("settings_rotate_p2", lang), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CustomWhite)
                        }
                        Switch(
                            checked = viewModel.rotateBoardForP2,
                            onCheckedChange = { viewModel.toggleRotateP2() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AmberCore,
                                checkedTrackColor = WoodBark
                            )
                        )
                    }
                }
            }

            // Language Selector
            item {
                Text(KatroStrings.get("settings_lang", lang), color = AmberGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.changeLanguage("fr") },
                        colors = CardDefaults.cardColors(containerColor = if (lang == "fr") WoodSatin else WoodDeepBrown),
                        border = if (lang == "fr") BorderStroke(1.5.dp, AmberCore) else null
                    ) {
                        Box(modifier = Modifier.padding(14.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Français", color = CustomWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.changeLanguage("mg") },
                        colors = CardDefaults.cardColors(containerColor = if (lang == "mg") WoodSatin else WoodDeepBrown),
                        border = if (lang == "mg") BorderStroke(1.5.dp, AmberCore) else null
                    ) {
                        Box(modifier = Modifier.padding(14.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Malagasy", color = CustomWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // AI difficulty picker
            item {
                Text(KatroStrings.get("settings_ai_diff", lang), color = AmberGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val difficulties = listOf(
                        Triple(Difficulty.BEGINNER, "diff_easy", Color.Green),
                        Triple(Difficulty.INTERMEDIATE, "diff_medium", AmberCore),
                        Triple(Difficulty.EXPERT, "diff_hard", Color.Red)
                    )

                    difficulties.forEach { (diff, resKey, accentColor) ->
                        val isSelected = viewModel.aiDifficulty == diff
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setAiDifficultySetting(diff) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) WoodWarmCaramel else WoodDeepBrown
                            ),
                            border = if (isSelected) BorderStroke(1.5.dp, accentColor) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(accentColor)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = KatroStrings.get(resKey, lang),
                                    color = if (isSelected) CustomWhite else WoodAsh,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Historical about context screen.
 */
@Composable
fun AboutScreen(viewModel: KatroViewModel) {
    val lang = viewModel.language

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.currentScreen = KatroScreen.HOME }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Menu", tint = WoodAsh)
            }
            Text(
                text = KatroStrings.get("about_title", lang),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = AmberGold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            colors = CardDefaults.cardColors(containerColor = WoodDeepBrown)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Katro Madagascar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold,
                        fontFamily = FontFamily.Serif
                    )
                }
                item { Text(text = KatroStrings.get("about_p1", lang), color = WoodAsh, fontSize = 14.sp, lineHeight = 19.sp) }
                item { Text(text = KatroStrings.get("about_p2", lang), color = WoodAsh, fontSize = 14.sp, lineHeight = 19.sp) }
                item { Text(text = KatroStrings.get("about_p3", lang), color = WoodAsh, fontSize = 14.sp, lineHeight = 19.sp) }
                item { Text(text = KatroStrings.get("about_p4", lang), color = AmberGold, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) }
            }
        }

        Button(
            onClick = { viewModel.currentScreen = KatroScreen.HOME },
            colors = ButtonDefaults.buttonColors(containerColor = WoodSatin),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(KatroStrings.get("back_to_menu", lang))
        }
    }
}
