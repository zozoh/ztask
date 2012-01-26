<ul id="__msg__" class="hdn">
<c:forEach var="m" items="${msg}">
	<li class="${fn:replace(m.key,'.','_')}">${m.value}</li>
</c:forEach>
</ul>