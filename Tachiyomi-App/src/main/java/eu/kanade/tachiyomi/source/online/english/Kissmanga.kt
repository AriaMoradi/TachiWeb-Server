package eu.kanade.tachiyomi.source.online.english

import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.model.*
import rx.Observable

class Kissmanga : Source {

    override val id: Long = 4

    override val name = "Kissmanga"

    override fun fetchMangaDetails(manga: SManga): Observable<SManga> {
        return Observable.error(Exception("We are happy to announce that Kissmanga is dead!"))
    }

    override fun fetchChapterList(manga: SManga): Observable<List<SChapter>> {
        return Observable.error(Exception("We are happy to announce that Kissmanga is dead!"))
    }

    override fun fetchPageList(chapter: SChapter): Observable<List<Page>> {
        return Observable.error(Exception("We are happy to announce that Kissmanga is dead!"))
    }

    override fun toString(): String {
        return "$name (EN)"
    }
}
