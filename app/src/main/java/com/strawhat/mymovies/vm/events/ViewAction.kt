package com.strawhat.mymovies.vm.events

sealed class ViewAction
data class LoadPageAction(val page: Int, val sortMode: SortMode) : ViewAction()
data class LoadFavoritesAction(val page: Int) : ViewAction()
object FavoritesActivatedAction : ViewAction()
object FavoritesDeActivatedAction : ViewAction()