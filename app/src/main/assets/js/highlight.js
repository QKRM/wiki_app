function highlightText(searchText) {
    if (!searchText) return;
    
    var walker = document.createTreeWalker(
        document.body,
        NodeFilter.SHOW_TEXT,
        {
            acceptNode: function(node) {
                // HTML 태그 내부의 텍스트 노드는 제외
                var parent = node.parentNode;
                while (parent) {
                    if (parent.nodeName === 'SCRIPT' || 
                        parent.nodeName === 'STYLE' || 
                        parent.nodeName === 'CODE' || 
                        parent.nodeName === 'PRE') {
                        return NodeFilter.FILTER_REJECT;
                    }
                    parent = parent.parentNode;
                }
                return NodeFilter.FILTER_ACCEPT;
            }
        },
        false
    );
    
    var nodesToReplace = [];
    var node;
    while (node = walker.nextNode()) {
        if (node.nodeValue.toLowerCase().indexOf(searchText.toLowerCase()) !== -1) {
            nodesToReplace.push(node);
        }
    }
    
    nodesToReplace.forEach(function(node) {
        var span = document.createElement('span');
        var text = node.nodeValue;
        var escapedSearchText = searchText.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
        var regex = new RegExp('(' + escapedSearchText + ')', 'gi');
        span.innerHTML = text.replace(regex, '<mark style="background-color: yellow; color: black;">$1</mark>');
        node.parentNode.replaceChild(span, node);
    });
} 