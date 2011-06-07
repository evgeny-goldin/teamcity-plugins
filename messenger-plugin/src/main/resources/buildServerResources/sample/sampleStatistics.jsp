<%@taglib prefix="stats" tagdir="/WEB-INF/tags/graph"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--<mystats:sampleFilter graphKey="${graphKey}"/>--%>
<stats:buildGraph id="SampleStatistics"
                  valueType="SampleStatistics"
                  height="150"
                  additionalFilter="/plugins/samplePlugin/sampleFilter.jsp"/>

