/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cmd3.app.filters

import com.intellij.util.io.URLUtil
import com.terminal.model.hyperlinks.HyperlinkFilter
import com.terminal.model.hyperlinks.LinkInfo
import com.terminal.model.hyperlinks.LinkResult
import com.terminal.model.hyperlinks.LinkResultItem
import java.awt.Desktop
import java.net.URI
import java.util.*

class UrlFilter : HyperlinkFilter {
    override fun apply(line: String): LinkResult? {
        if (!URLUtil.canContainUrl(line)) return null
        val textStartOffset = 0
        val m = URLUtil.URL_PATTERN.matcher(line)
        var item: LinkResultItem? = null
        var items: MutableList<LinkResultItem?>? = null
        while (m.find()) {
            if (item != null) {
                if (items == null) {
                    items = ArrayList(2)
                    items.add(item)
                }
            }
            val url = m.group()
            item = LinkResultItem(textStartOffset + m.start(), textStartOffset + m.end(), LinkInfo {
                try {
                    Desktop.getDesktop().browse(URI(url))
                } catch (e: Exception) {
                    //pass
                }
            })
            items?.add(item)
        }
        return items?.let { LinkResult(it) } ?: item?.let { LinkResult(it) }
    }
}