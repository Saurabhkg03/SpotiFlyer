package com.shabinder.common.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shabinder.common.DownloadStatus
import com.shabinder.common.TrackDetails
import com.shabinder.common.ui.ImageLoad
import com.shabinder.spotiflyer.ui.SpotiFlyerTypography
import com.shabinder.spotiflyer.ui.colorAccent
import kotlinx.coroutines.CoroutineScope

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
                    CoverImage(result.title, result.coverUrl, coroutineScope)
                }
                itemsIndexed(result.trackList) { index, item ->
                    TrackCard(
                        track = item,
                        downloadTrack = { component.onDownloadClicked(result.trackList,index) },
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
) {
    Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        ImageLoad(
            url = track.albumArtURL,
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
                //Image(vectorResource(id = R.drawable.ic_tick))
            }
            DownloadStatus.Queued -> {
                CircularProgressIndicator()
            }
            DownloadStatus.Failed -> {
                //Image(vectorResource(id = R.drawable.ic_error))
            }
            DownloadStatus.Downloading -> {
                CircularProgressIndicator(progress = track.progress.toFloat()/100f)
            }
            DownloadStatus.Converting -> {
                CircularProgressIndicator(progress = 100f,color = colorAccent)
            }
            DownloadStatus.NotDownloaded -> {
                /*Image(vectorResource(id = R.drawable.ic_arrow), Modifier.clickable(onClick = {
                    downloadTrack()
                }))*/
            }
        }
    }
}

@Composable
fun CoverImage(
    title: String,
    coverURL: String,
    scope: CoroutineScope,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.padding(vertical = 8.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageLoad(
            url = coverURL,
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
        //icon = { Icon(imageVector = Image(R.drawable.ic_download_arrow),tint = Color.Black) },
        backgroundColor = colorAccent,
        modifier = modifier
    )
}