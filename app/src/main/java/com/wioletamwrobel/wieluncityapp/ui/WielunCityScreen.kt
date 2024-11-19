package com.wioletamwrobel.wieluncityapp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import androidx.media3.ui.PlayerView
import com.wioletamwrobel.wieluncityapp.R
import com.wioletamwrobel.wieluncityapp.model.Dialog
import com.wioletamwrobel.wieluncityapp.model.Place
import com.wioletamwrobel.wieluncityapp.ui.WielunCityViewModel.WielunCityUiState
import com.wioletamwrobel.wieluncityapp.ui.theme.Shapes
import com.wioletamwrobel.wieluncityapp.utils.PlaceActionType
import com.wioletamwrobel.wieluncityapp.utils.PlacesContentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//main function responsible for displaying main screen of the app depending on window size
@Composable
fun WielunCityApp(
    onBackPressed: () -> Unit,
    windowSize: WindowWidthSizeClass,
    context: Context,
    activity: Activity,
    prefs: SharedPreferences,
    viewModel: WielunCityViewModel,
    uiState: State<WielunCityUiState>,

    ) {
    val contentType = when (windowSize) {
        WindowWidthSizeClass.Compact, WindowWidthSizeClass.Medium -> PlacesContentType.LIST_ONLY
        WindowWidthSizeClass.Expanded -> PlacesContentType.LIST_AND_DETAIL
        else -> PlacesContentType.LIST_ONLY
    }

    val scope = CoroutineScope(Dispatchers.Main)

    LaunchedEffect(key1 = true) {
        scope.launch {
            while (true) {
                withContext(Dispatchers.IO) {
                    delay(2000)
                    viewModel.scannerResponseWithoutButton(context, activity)
                    withContext(Dispatchers.Main) {
                        viewModel.cleanScannedBeacon()
                    }
                }
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            onBackButtonClick = {
                viewModel.navigateToListPage()
                viewModel.stopAudio()
                viewModel.clearPlayer()
                viewModel.startMovie()
            },
            onScannerButtonClick = {
                viewModel.startScanning(context, activity)
                viewModel.navigateToDialog()
            },
            isShowingListPage = uiState.value.isShowingListPage,
            selectedPlace = uiState.value.currentPlace,
            windowSize = windowSize,
            uiState = uiState,
        )
    }) { innerPadding ->
        if (contentType == PlacesContentType.LIST_AND_DETAIL) {
            PlaceListAndDetail(
                places = uiState.value.placesList,
                onClick = { viewModel.updateCurrentPlace(it) },
                selectedPlace = uiState.value.currentPlace,
                contentPadding = innerPadding,
                onBackPressed = onBackPressed,
                prefs = prefs,
                context = context,
                viewModel = viewModel,
                uiState = uiState
            )
        } else {
            if (uiState.value.isShowingListPage) {
                PlaceListLazyColumn(
                    places = uiState.value.placesList, onClick = {
                        viewModel.updateCurrentPlace(it)
                        viewModel.navigateToDetailPage()
                    }, contentPadding = innerPadding,
                    prefs = prefs
                )
            } else {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(
                            top = dimensionResource(id = R.dimen.extra_large),
                            bottom = dimensionResource(id = R.dimen.large25),
                            start = dimensionResource(id = R.dimen.medium),
                            end = dimensionResource(id = R.dimen.medium)
                        )
                ) {
                    PlaceDetail(
                        selectedPlace = uiState.value.currentPlace,
                        onBackPressed = {
                            viewModel.navigateToListPage()
                            viewModel.cleanScannedBeacon()
                        },
                        contentPadding = innerPadding,
                        modifier = Modifier,
                        imageHeight = dimensionResource(id = R.dimen.place_detail_image_height),
                        contentScale = ContentScale.Crop,
                        context = context,
                        viewModel = viewModel,
                        uiState = uiState
                    )
                }
            }
        }
    }
    if (uiState.value.isScannerButtonClicked) {
        Dialog.CreateDialog(
            icon = {
                if (uiState.value.isScannerLoading) {
                    CircularProgressIndicator()
                } else {
                    Icon(
                        Icons.Filled.Search,
                        stringResource(R.string.search_icon_description),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(35.dp)
                    )
                }
            },
            title =
            if (uiState.value.isScannerLoading) {
                stringResource(R.string.search_dialog_title_loading)
            } else {
                stringResource(R.string.search_dialog_title)
            },
            dialogText = stringResource(R.string.search_dialog_text),
            onConfirmButtonClicked = {
                viewModel.scannerLoading()
                viewModel.scannerButtonResponse(context, activity)
            },
            onConfirmButtonText = if (uiState.value.isScannerLoading) stringResource(R.string.confirm_button_refresh) else stringResource(
                R.string.confirm_button_search
            ),
            onDismissButtonClicked = {
                viewModel.navigateFromDialog()
                viewModel.stopLoading()
                viewModel.cleanScannedBeacon()
            },
            onDismissButtonText = stringResource(R.string.dialog_dismissButton_text)
        )
    }
}

//top app bar in placeAndDetail view displaying "Places to visit", in detail view only place category and a backButton
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    onBackButtonClick: () -> Unit,
    onScannerButtonClick: () -> Unit,
    isShowingListPage: Boolean,
    selectedPlace: Place,
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    uiState: State<WielunCityUiState>,
) {
    val isShowingDetailPage = windowSize != WindowWidthSizeClass.Expanded && !isShowingListPage

    TopAppBar(title = {
        Text(
            text = if (isShowingDetailPage) stringResource(id = selectedPlace.placeCategory)
            else stringResource(R.string.app_bar),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.secondary
        )
    },
        navigationIcon = if (isShowingDetailPage || uiState.value.isBeaconScanned) {
            {
                IconButton(onClick = onBackButtonClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            {
                IconButton(onClick = onScannerButtonClick) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Scanner Icon",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        },
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = dimensionResource(id = R.dimen.medium10)))
}

//UI for item in lazyColumn list with places to visit
@Composable
fun PlaceListItem(
    place: Place,
    onClick: (Place) -> Unit,
) {
    Card(elevation = CardDefaults.cardElevation(dimensionResource(id = R.dimen.extra_small)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = Shapes.large,
        modifier = Modifier
            .padding(vertical = dimensionResource(id = R.dimen.small))
            .fillMaxWidth()
            .width(dimensionResource(id = R.dimen.place_list_item_card_width))
            .height(dimensionResource(id = R.dimen.place_list_item_card_height))
            .border(
                dimensionResource(id = R.dimen.border_width),
                color = MaterialTheme.colorScheme.primary,
                Shapes.large
            ),
        onClick = { onClick(place) }) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(Shapes.large)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(2f)
                    .padding(dimensionResource(id = R.dimen.medium18))
                    .fillMaxHeight()
            ) {
                Text(
                    text = stringResource(place.nameResource),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.wrapContentSize(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                HorizontalDivider(modifier = Modifier.height(dimensionResource(id = R.dimen.divider_height)))
                Text(
                    text = stringResource(place.localizationResource),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.extra_small))
                        .wrapContentSize(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            PlaceImageListItem(
                place = place, contentScale = ContentScale.Crop, modifier = Modifier.weight(1f)
            )
        }
    }
}

//Function for blackAndWhite image in placeList lazyColumn
@Composable
fun PlaceImageListItem(
    place: Place, contentScale: ContentScale, modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(place.placeImageResourceSmall),
            contentDescription = stringResource(place.nameResource),
            alignment = Alignment.Center,
            contentScale = contentScale
        )
    }
}

//Function for loading place list in LazyColumn
@OptIn(FlowPreview::class)
@Composable
fun PlaceListLazyColumn(
    places: List<Place>,
    prefs: SharedPreferences,
    onClick: (Place) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val scrollPosition = prefs.getInt("scroll_position", 0)
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = scrollPosition)

    LaunchedEffect(key1 = lazyListState) {
        snapshotFlow {
            lazyListState.firstVisibleItemIndex
        }.debounce(400L).collectLatest { index ->
            prefs.edit().putInt("scroll_position", index).apply()
        }
    }

    LazyColumn(
        state = lazyListState,
        contentPadding = contentPadding,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = dimensionResource(id = R.dimen.medium))
    ) {
        items(
            places, //key = { place -> place.id }
        ) { place ->
            PlaceListItem(
                place = place,
                onClick = onClick,
            )
        }
    }
}

//Function for displaying image in detail pages
@Composable
fun PlaceDetailImage(
    selectedPlace: Place, contentScale: ContentScale, modifier: Modifier
) {
    Image(
        painter = painterResource(selectedPlace.placeImageResource),
        contentDescription = stringResource(selectedPlace.nameResource),
        contentScale = contentScale,
        modifier = modifier
    )
}

//Function for displaying details about places
@Composable
fun PlaceDetail(
    selectedPlace: Place,
    contentPadding: PaddingValues,
    onBackPressed: () -> Unit,
    imageHeight: Dp,
    contentScale: ContentScale,
    context: Context,
    viewModel: WielunCityViewModel,
    uiState: State<WielunCityUiState>,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackPressed() }


    if (selectedPlace.placeAction != null) {
        DetailPlaceAction(
            selectedPlace,
            viewModel,
            uiState,
            context,
        )
    }

    val scrollState = rememberScrollState()
    val layoutDirection = LocalLayoutDirection.current
    val intent = Intent(Intent.ACTION_VIEW)

    Card(
        modifier = Modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(dimensionResource(id = R.dimen.extra_small)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = Shapes.large,
    ) {
        Box(
            modifier = modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(
                    bottom = dimensionResource(id = R.dimen.medium10),
                    start = contentPadding.calculateStartPadding(layoutDirection),
                    end = contentPadding.calculateEndPadding(layoutDirection)
                )
            ) {
                PlaceDetailImage(
                    selectedPlace = selectedPlace,
                    modifier = Modifier.height(imageHeight),
                    contentScale = contentScale
                )

                Text(
                    text = stringResource(selectedPlace.nameResource),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.medium))
                )
                HorizontalDivider(
                    modifier = Modifier
                        .height(dimensionResource(id = R.dimen.divider_height))
                        .padding(horizontal = dimensionResource(id = R.dimen.medium))
                )
                Text(
                    text = stringResource(selectedPlace.descriptionResource),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Justify,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.medium))
                )
                Row(
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Text(text = "${stringResource(id = R.string.localization)}: ${
                        stringResource(
                            selectedPlace.localizationResource
                        )
                    }",
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Justify,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.medium))
                            .clickable {
                                intent.apply {
                                    data = Uri.parse(selectedPlace.geolocation)
                                    startActivity(context, intent, null)
                                }
                            })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPlaceAction(
    place: Place,
    viewModel: WielunCityViewModel,
    uiState: State<WielunCityUiState>,
    context: Context,
) {

    var showDialog by remember { mutableStateOf(true) }
    val intentActionView = Intent(Intent.ACTION_VIEW)

    if (place.placeAction != null) {
        when (place.placeActionType) {
            PlaceActionType.AUDIO -> {
                LaunchedEffect(key1 = true) {
                    viewModel.startAudio(place.placeAction.toString().toInt(), context)
                }
            }

            PlaceActionType.PAGE -> {}
            PlaceActionType.MOVIE -> {
                if (uiState.value.isMovieToPlay) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            viewModel.stopMovie()
                            viewModel.navigateToDetailPage()
                        },
                        sheetState = rememberModalBottomSheetState()
                    ) {
                        AndroidView(
                            factory = {
                                PlayerView(context).apply {
                                    player = viewModel.getPlayer(
                                        place.placeAction.toString().toInt(),
                                        context
                                    )
                                    //   player?.play()
                                }
                            }, modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16 / 9f)
                        )
                    }
                }
            }

            PlaceActionType.NOTIFICATION -> {}
            PlaceActionType.DIALOG -> {
                if (showDialog) {
                    Dialog.CreateDialog(
                        icon = {
                            Icon(
                                Icons.Filled.Info,
                                stringResource(R.string.alert_dialog_cinema_info_icon_description),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(35.dp)
                            )
                        },
                        title = stringResource(R.string.alert_dialog_cinema_title),
                        dialogText = stringResource(R.string.alert_dialog_cinema_text),
                        onConfirmButtonClicked = {
                            intentActionView.apply {
                                data = Uri.parse(place.placeAction.toString())
                                startActivity(context, intentActionView, null)
                            }
                            showDialog = false
                        },
                        onConfirmButtonText = stringResource(R.string.alert_dialog_cinema_confirmButton),
                        onDismissButtonClicked = { showDialog = false },
                        onDismissButtonText = stringResource(R.string.alert_dialog_cinema_dismissButton)
                    )
                }
            }

            null -> {}
        }
    }
}

//Function for displaying main screen - place and detail view for larger screen sizes
@Composable
fun PlaceListAndDetail(
    places: List<Place>,
    onClick: (Place) -> Unit,
    prefs: SharedPreferences,
    selectedPlace: Place,
    onBackPressed: () -> Unit,
    context: Context,
    viewModel: WielunCityViewModel,
    uiState: State<WielunCityUiState>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .padding(
                    top = dimensionResource(id = R.dimen.extra_large),
                    start = dimensionResource(id = R.dimen.border_width),
                    end = dimensionResource(id = R.dimen.border_width)
                )
                .weight(3f)
        ) {
            PlaceListLazyColumn(
                places = places, onClick = onClick, prefs = prefs
            )
        }
        Box(
            modifier = Modifier
                .weight(4f)
                .padding(
                    top = dimensionResource(id = R.dimen.extra_large),
                    bottom = dimensionResource(id = R.dimen.large),
                    end = dimensionResource(id = R.dimen.medium)
                )
        ) {
            PlaceDetail(
                selectedPlace = selectedPlace,
                contentPadding = contentPadding,
                onBackPressed = onBackPressed,
                imageHeight = dimensionResource(id = R.dimen.place_and_detail_image_height),
                contentScale = ContentScale.Crop,
                context = context,
                viewModel = viewModel,
                uiState = uiState
            )
        }
    }
}



