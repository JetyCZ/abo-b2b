package cz.abo.b2b.web.component

import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.HasText
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.html.Span


/**
 * A component to show HTML text.
 *
 * @author Syam
 */
class StyledText(htmlText: String) : Composite<Span>(),
    HasText {
        private val content = Span()
        private var text: String? = null
        override fun initContent(): Span {
            return content
    }

    override fun setText(htmlText: String) {
        var htmlText: String? = htmlText
        if (htmlText == null) {
            htmlText = ""
        }
        if (htmlText == text) {
            return
        }
        text = htmlText
        content.removeAll()
        content.add(Html("<span>$htmlText</span>"))
    }

    override fun getText(): String {
        return text!!
    }

    init {
        setText(htmlText)
    }
}