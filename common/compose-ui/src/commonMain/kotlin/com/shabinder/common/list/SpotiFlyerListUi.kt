package com.shabinder.common.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shabinder.common.models.DownloadStatus
import com.shabinder.common.models.TrackDetails
import com.shabinder.common.ui.*
import com.shabinder.common.ui.SpotiFlyerTypography
import com.shabinder.common.ui.colorAccent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SpotiFlyerListContent(
    component: SpotiFlyerList,
    modifier: Modifier = Modifier
) {
    val model by component.models.collectAsState(SpotiFlyerList.State())
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        //TODO Null Handling
        val result = model.queryResult!!

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = {
                item {
                    CoverImage(result.title, result.coverUrl, coroutineScope,component::loadImage)
                }
                itemsIndexed(result.trackList) { index, item ->
                    TrackCard(
                        track = item,
                        downloadTrack = { component.onDownloadClicked(result.trackList,index) },
                        loadImage = component::loadImage
                    )
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
        DownloadAllButton(
            onClick = {component.onDownloadAllClicked(result.trackList)},
            modifier = Modifier.padding(bottom = 24.dp).align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun TrackCard(
    track: TrackDetails,
    downloadTrack:()->Unit,
    loadImage:suspend (String)-> ImageBitmap?
) {
    Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        var pic by mutableStateOf<ImageBitmap?>(null)
        val scope = rememberCoroutineScope()
        scope.launch {
            pic = loadImage(track.albumArtURL)
        }
        ImageLoad(
            pic = pic,
            "Album Art",
            modifier = Modifier
                .preferredWidth(75.dp)
                .preferredHeight(90.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Column(modifier = Modifier.padding(horizontal = 8.dp).preferredHeight(60.dp).weight(1f),verticalArrangement = Arrangement.SpaceEvenly) {
            Text(track.title,maxLines = 1,overflow = TextOverflow.Ellipsis,style = SpotiFlyerTypography.h6,color = colorAccent)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(horizontal = 8.dp).fillMaxSize()
            ){
                Text("${track.artists.firstOrNull()}...",fontSize = 12.sp,maxLines = 1)
                Text("${track.durationSec/60} min, ${track.durationSec%60} sec",fontSize = 12.sp,maxLines = 1,overflow = TextOverflow.Ellipsis)
            }
        }
        when(track.downloaded){
            DownloadStatus.Downloaded -> {
                DownloadImageTick()
            }
            DownloadStatus.Queued -> {
                CircularProgressIndicator()
            }
            DownloadStatus.Failed -> {
                DownloadImageError()
            }
            DownloadStatus.Downloading -> {
                CircularProgressIndicator(progress = track.progress.toFloat()/100f)
            }
            DownloadStatus.Converting -> {
                CircularProgressIndicator(progress = 100f,color = colorAccent)
            }
            DownloadStatus.NotDownloaded -> {
                DownloadImageArrow(Modifier.clickable(onClick = {
                    downloadTrack()
                }))
            }
        }
    }
}

@Composable
fun CoverImage(
    title: String,
    coverURL: String,
    scope: CoroutineScope,
    loadImage: suspend (String) -> ImageBitmap?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.padding(vertical = 8.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var pic by mutableStateOf<ImageBitmap?>(null)
        scope.launch {
            pic = loadImage(coverURL)
        }
        ImageLoad(
            pic,
            "Cover Image",
            modifier = Modifier
                .preferredWidth(210.dp)
                .preferredHeight(230.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Text(
            text = title,
            style = SpotiFlyerTypography.h5,
            maxLines = 2,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            //color = colorAccent,
        )
    }
    /*scope.launch {
        updateGradient(coverURL, ctx)
    }*/
}

@Composable
fun DownloadAllButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ExtendedFloatingActionButton(
        text = { Text("Download All") },
        onClick = onClick,
        icon = { Icon(imageVector = DownloadAllImage(),"Download All Button",tint = Color(0xFF000000)) },
        backgroundColor = colorAccent,
        modifier = modifier
    )
}