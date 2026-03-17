
package demotrf5.trf5_0_1;

import routines.Numeric;
import routines.DataOperation;
import routines.TalendDataGenerator;
import routines.TalendStringUtil;
import routines.TalendString;
import routines.MDM;
import routines.StringHandling;
import routines.Relational;
import routines.TalendDate;
import routines.Mathematical;
import routines.SQLike;
import routines.system.*;
import routines.system.api.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Comparator;

@SuppressWarnings("unused")

/**
 * Job: TRF5 Purpose: <br>
 * Description: <br>
 * 
 * @author tiago.araujo@trial.hrr6jkxwl7e1fqq.us.qlikcloud.com
 * @version 8.0.1.20260211_0926-patch
 * @status
 */
public class TRF5 implements TalendJob {
	static {
		System.setProperty("TalendJob.log", "TRF5.log");
	}

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(TRF5.class);

	static {
		System.setProperty("talend.component.record.nullable.check", "true");
		String javaUtilLoggingConfigFile = System.getProperty("java.util.logging.config.file");
		if (javaUtilLoggingConfigFile == null) {
			setupDefaultJavaUtilLogging();
		}
	}

	/**
	 * This class replaces the default {@code System.err} stream used by Java Util
	 * Logging (JUL). You can use your own configuration through the
	 * {@code java.util.logging.config.file} system property, enabling you to
	 * specify an external logging configuration file for tailored logging setup.
	 */
	public static class StandardConsoleHandler extends java.util.logging.StreamHandler {
		public StandardConsoleHandler() {
			// Set System.out as default log output stream
			super(System.out, new java.util.logging.SimpleFormatter());
		}

		/**
		 * Publish a {@code LogRecord}. The logging request was made initially to a
		 * {@code Logger} object, which initialized the {@code LogRecord} and forwarded
		 * it here.
		 *
		 * @param record description of the log event. A null record is silently ignored
		 *               and is not published
		 */
		@Override
		public void publish(java.util.logging.LogRecord record) {
			super.publish(record);
			flush();
		}

		/**
		 * Override {@code StreamHandler.close} to do a flush but not to close the
		 * output stream. That is, we do <b>not</b> close {@code System.out}.
		 */
		@Override
		public void close() {
			flush();
		}
	}

	protected static void setupDefaultJavaUtilLogging() {
		java.util.logging.LogManager logManager = java.util.logging.LogManager.getLogManager();

		// Get the root logger
		java.util.logging.Logger rootLogger = logManager.getLogger("");

		// Remove existing handlers to set standard console handler only
		java.util.logging.Handler[] handlers = rootLogger.getHandlers();
		for (java.util.logging.Handler handler : handlers) {
			rootLogger.removeHandler(handler);
		}

		rootLogger.addHandler(new StandardConsoleHandler());
		rootLogger.setLevel(java.util.logging.Level.INFO);
	}

	protected static boolean isCBPClientPresent() {
		boolean isCBPClientPresent = false;
		try {
			Class.forName("org.talend.metrics.CBPClient");
			isCBPClientPresent = true;
		} catch (java.lang.ClassNotFoundException e) {
		}
		return isCBPClientPresent;
	}

	protected static void logIgnoredError(String message, Throwable cause) {
		log.error(message, cause);

	}

	public final Object obj = new Object();

	// for transmiting parameters purpose
	private Object valueObject = null;

	public Object getValueObject() {
		return this.valueObject;
	}

	public void setValueObject(Object valueObject) {
		this.valueObject = valueObject;
	}

	private final static String defaultCharset = java.nio.charset.Charset.defaultCharset().name();

	private final static String utf8Charset = "UTF-8";

	public static String taskExecutionId = null;

	public static String jobExecutionId = java.util.UUID.randomUUID().toString();;

	private final static boolean isCBPClientPresent = isCBPClientPresent();

	public static final java.util.List<Thread> threadList = java.util.Collections
			.synchronizedList(new java.util.ArrayList<>());

	// contains type for every context property
	public class PropertiesWithType extends java.util.Properties {
		private static final long serialVersionUID = 1L;
		private java.util.Map<String, String> propertyTypes = new java.util.HashMap<>();

		public PropertiesWithType(java.util.Properties properties) {
			super(properties);
		}

		public PropertiesWithType() {
			super();
		}

		public void setContextType(String key, String type) {
			propertyTypes.put(key, type);
		}

		public String getContextType(String key) {
			return propertyTypes.get(key);
		}
	}

	// create and load default properties
	private java.util.Properties defaultProps = new java.util.Properties();

	// create application properties with default
	public class ContextProperties extends PropertiesWithType {

		private static final long serialVersionUID = 1L;

		public ContextProperties(java.util.Properties properties) {
			super(properties);
		}

		public ContextProperties() {
			super();
		}

		public void synchronizeContext() {

		}

		// if the stored or passed value is "<TALEND_NULL>" string, it mean null
		public String getStringValue(String key) {
			String origin_value = this.getProperty(key);
			if (NULL_VALUE_EXPRESSION_IN_COMMAND_STRING_FOR_CHILD_JOB_ONLY.equals(origin_value)) {
				return null;
			}
			return origin_value;
		}

	}

	protected ContextProperties context = new ContextProperties(); // will be instanciated by MS.

	public ContextProperties getContext() {
		return this.context;
	}

	protected java.util.Map<String, String> defaultProperties = new java.util.HashMap<String, String>();
	protected java.util.Map<String, String> additionalProperties = new java.util.HashMap<String, String>();

	public java.util.Map<String, String> getDefaultProperties() {
		return this.defaultProperties;
	}

	public java.util.Map<String, String> getAdditionalProperties() {
		return this.additionalProperties;
	}

	private final String jobVersion = "0.1";
	private final String jobName = "TRF5";
	private final String projectName = "DEMOTRF5";
	public Integer errorCode = null;
	private String currentComponent = "";
	public static boolean isStandaloneMS = Boolean.valueOf("false");

	private void s(final String component) {
		try {
			org.talend.metrics.DataReadTracker.setCurrentComponent(jobName, component);
		} catch (Exception | NoClassDefFoundError e) {
			// ignore
		}
	}

	private void mdc(final String subJobName, final String subJobPidPrefix) {
		mdcInfo.forEach(org.slf4j.MDC::put);
		org.slf4j.MDC.put("_subJobName", subJobName);
		org.slf4j.MDC.put("_subJobPid", subJobPidPrefix + subJobPidCounter.getAndIncrement());
	}

	private void sh(final String componentId) {
		ok_Hash.put(componentId, false);
		start_Hash.put(componentId, System.currentTimeMillis());
	}

	{
		s("none");
	}

	private String cLabel = null;

	private final java.util.Map<String, Object> globalMap = new java.util.HashMap<String, Object>();
	private final static java.util.Map<String, Object> junitGlobalMap = new java.util.HashMap<String, Object>();

	private final java.util.Map<String, Long> start_Hash = new java.util.HashMap<String, Long>();
	private final java.util.Map<String, Long> end_Hash = new java.util.HashMap<String, Long>();
	private final java.util.Map<String, Boolean> ok_Hash = new java.util.HashMap<String, Boolean>();
	public final java.util.List<String[]> globalBuffer = new java.util.ArrayList<String[]>();

	private final JobStructureCatcherUtils talendJobLog = new JobStructureCatcherUtils(jobName,
			"_mceAMB7SEfG99Y5mkdgJFg", "0.1");
	private org.talend.job.audit.JobAuditLogger runtime_lineage_logger_talendJobLog = null;
	private org.talend.job.audit.JobAuditLogger auditLogger_talendJobLog = null;

	private RunStat runStat = new RunStat(talendJobLog, System.getProperty("audit.interval"));

	// OSGi DataSource
	private final static String KEY_DB_DATASOURCES = "KEY_DB_DATASOURCES";

	private final static String KEY_DB_DATASOURCES_RAW = "KEY_DB_DATASOURCES_RAW";

	public void setDataSources(java.util.Map<String, javax.sql.DataSource> dataSources) {
		java.util.Map<String, routines.system.TalendDataSource> talendDataSources = new java.util.HashMap<String, routines.system.TalendDataSource>();
		for (java.util.Map.Entry<String, javax.sql.DataSource> dataSourceEntry : dataSources.entrySet()) {
			talendDataSources.put(dataSourceEntry.getKey(),
					new routines.system.TalendDataSource(dataSourceEntry.getValue()));
		}
		globalMap.put(KEY_DB_DATASOURCES, talendDataSources);
		globalMap.put(KEY_DB_DATASOURCES_RAW, new java.util.HashMap<String, javax.sql.DataSource>(dataSources));
	}

	public void setDataSourceReferences(List serviceReferences) throws Exception {

		java.util.Map<String, routines.system.TalendDataSource> talendDataSources = new java.util.HashMap<String, routines.system.TalendDataSource>();
		java.util.Map<String, javax.sql.DataSource> dataSources = new java.util.HashMap<String, javax.sql.DataSource>();

		for (java.util.Map.Entry<String, javax.sql.DataSource> entry : BundleUtils
				.getServices(serviceReferences, javax.sql.DataSource.class).entrySet()) {
			dataSources.put(entry.getKey(), entry.getValue());
			talendDataSources.put(entry.getKey(), new routines.system.TalendDataSource(entry.getValue()));
		}

		globalMap.put(KEY_DB_DATASOURCES, talendDataSources);
		globalMap.put(KEY_DB_DATASOURCES_RAW, new java.util.HashMap<String, javax.sql.DataSource>(dataSources));
	}

	private final java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
	private final java.io.PrintStream errorMessagePS = new java.io.PrintStream(new java.io.BufferedOutputStream(baos));

	public String getExceptionStackTrace() {
		if ("failure".equals(this.getStatus())) {
			errorMessagePS.flush();
			return baos.toString();
		}
		return null;
	}

	private Exception exception;

	public Exception getException() {
		if ("failure".equals(this.getStatus())) {
			return this.exception;
		}
		return null;
	}

	private class TalendException extends Exception {

		private static final long serialVersionUID = 1L;

		private java.util.Map<String, Object> globalMap = null;
		private Exception e = null;

		private String currentComponent = null;
		private String cLabel = null;

		private String virtualComponentName = null;

		public void setVirtualComponentName(String virtualComponentName) {
			this.virtualComponentName = virtualComponentName;
		}

		private TalendException(Exception e, String errorComponent, final java.util.Map<String, Object> globalMap) {
			this.currentComponent = errorComponent;
			this.globalMap = globalMap;
			this.e = e;
		}

		private TalendException(Exception e, String errorComponent, String errorComponentLabel,
				final java.util.Map<String, Object> globalMap) {
			this(e, errorComponent, globalMap);
			this.cLabel = errorComponentLabel;
		}

		public Exception getException() {
			return this.e;
		}

		public String getCurrentComponent() {
			return this.currentComponent;
		}

		public String getExceptionCauseMessage(Exception e) {
			Throwable cause = e;
			String message = null;
			int i = 10;
			while (null != cause && 0 < i--) {
				message = cause.getMessage();
				if (null == message) {
					cause = cause.getCause();
				} else {
					break;
				}
			}
			if (null == message) {
				message = e.getClass().getName();
			}
			return message;
		}

		@Override
		public void printStackTrace() {
			if (!(e instanceof TalendException || e instanceof TDieException)) {
				if (virtualComponentName != null && currentComponent.indexOf(virtualComponentName + "_") == 0) {
					globalMap.put(virtualComponentName + "_ERROR_MESSAGE", getExceptionCauseMessage(e));
				}
				globalMap.put(currentComponent + "_ERROR_MESSAGE", getExceptionCauseMessage(e));
				System.err.println("Exception in component " + currentComponent + " (" + jobName + ")");
			}
			if (!(e instanceof TDieException)) {
				if (e instanceof TalendException) {
					e.printStackTrace();
				} else {
					e.printStackTrace();
					e.printStackTrace(errorMessagePS);
				}
			}
			if (!(e instanceof TalendException)) {
				TRF5.this.exception = e;
			}
			if (!(e instanceof TalendException)) {
				try {
					for (java.lang.reflect.Method m : this.getClass().getEnclosingClass().getMethods()) {
						if (m.getName().compareTo(currentComponent + "_error") == 0) {
							m.invoke(TRF5.this, new Object[] { e, currentComponent, globalMap });
							break;
						}
					}

					if (!(e instanceof TDieException)) {
						if (enableLogStash) {
							talendJobLog.addJobExceptionMessage(currentComponent, cLabel, null, e);
							talendJobLogProcess(globalMap);
						}
					}
				} catch (Exception e) {
					this.e.printStackTrace();
				}
			}
		}
	}

	public void Processo_Silver_1_tS3Connection_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		Processo_Silver_1_tS3Connection_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void Processo_Silver_1_tS3List_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		Processo_Silver_1_tS3List_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void Processo_Silver_1_tS3Get_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		Processo_Silver_1_tS3List_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void Processos_Ingestao_Bronze_1_tDBInput_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		Processos_Ingestao_Bronze_1_tDBInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void Processos_Ingestao_Bronze_1_tFileOutputParquet_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		Processos_Ingestao_Bronze_1_tDBInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void Processos_Ingestao_Bronze_1_tDBInput_2_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		Processos_Ingestao_Bronze_1_tDBInput_2_onSubJobError(exception, errorComponent, globalMap);
	}

	public void Processos_Ingestao_Bronze_1_tFileOutputParquet_2_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		Processos_Ingestao_Bronze_1_tDBInput_2_onSubJobError(exception, errorComponent, globalMap);
	}

	public void Processos_Ingestao_Bronze_1_tFileList_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		Processos_Ingestao_Bronze_1_tFileList_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void Processos_Ingestao_Bronze_1_tS3Put_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		Processos_Ingestao_Bronze_1_tFileList_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void Processos_Ingestao_Bronze_1_tDBInput_3_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		Processos_Ingestao_Bronze_1_tDBInput_3_onSubJobError(exception, errorComponent, globalMap);
	}

	public void Processos_Ingestao_Bronze_1_tFileOutputParquet_3_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		Processos_Ingestao_Bronze_1_tDBInput_3_onSubJobError(exception, errorComponent, globalMap);
	}

	public void Processos_Ingestao_Bronze_1_tDBInput_4_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		Processos_Ingestao_Bronze_1_tDBInput_4_onSubJobError(exception, errorComponent, globalMap);
	}

	public void Processos_Ingestao_Bronze_1_tFileOutputParquet_4_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		Processos_Ingestao_Bronze_1_tDBInput_4_onSubJobError(exception, errorComponent, globalMap);
	}

	public void Processos_Ingestao_Bronze_1_tDBInput_5_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		Processos_Ingestao_Bronze_1_tDBInput_5_onSubJobError(exception, errorComponent, globalMap);
	}

	public void Processos_Ingestao_Bronze_1_tFileOutputParquet_5_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		Processos_Ingestao_Bronze_1_tDBInput_5_onSubJobError(exception, errorComponent, globalMap);
	}

	public void Processos_Ingestao_Bronze_1_tS3Connection_2_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		Processos_Ingestao_Bronze_1_tS3Connection_2_onSubJobError(exception, errorComponent, globalMap);
	}

	public void talendJobLog_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		talendJobLog_onSubJobError(exception, errorComponent, globalMap);
	}

	public void Processo_Silver_1_tS3Connection_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void Processo_Silver_1_tS3List_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void Processos_Ingestao_Bronze_1_tDBInput_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void Processos_Ingestao_Bronze_1_tDBInput_2_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void Processos_Ingestao_Bronze_1_tFileList_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void Processos_Ingestao_Bronze_1_tDBInput_3_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void Processos_Ingestao_Bronze_1_tDBInput_4_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void Processos_Ingestao_Bronze_1_tDBInput_5_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void Processos_Ingestao_Bronze_1_tS3Connection_2_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void talendJobLog_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void Processo_Silver_1_tS3Connection_1Process(final java.util.Map<String, Object> globalMap)
			throws TalendException {
		globalMap.put("Processo_Silver_1_tS3Connection_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("Processo_Silver_1_tS3Connection_1", "CseGaL_");

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				/**
				 * [Processo_Silver_1_tS3Connection_1 begin ] start
				 */

				sh("Processo_Silver_1_tS3Connection_1");

				s(currentComponent = "Processo_Silver_1_tS3Connection_1");

				int tos_count_Processo_Silver_1_tS3Connection_1 = 0;

				if (log.isDebugEnabled())
					log.debug("Processo_Silver_1_tS3Connection_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_Processo_Silver_1_tS3Connection_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_Processo_Silver_1_tS3Connection_1 = new StringBuilder();
							log4jParamters_Processo_Silver_1_tS3Connection_1.append("Parameters:");
							log4jParamters_Processo_Silver_1_tS3Connection_1
									.append("configuration.credentialProvider" + " = " + "STATIC_CREDENTIALS");
							log4jParamters_Processo_Silver_1_tS3Connection_1.append(" | ");
							log4jParamters_Processo_Silver_1_tS3Connection_1
									.append("configuration.staticCredentialConfiguration.accessKey" + " = "
											+ "AKIAUWW4VRZLOLIPY4ZH");
							log4jParamters_Processo_Silver_1_tS3Connection_1.append(" | ");
							log4jParamters_Processo_Silver_1_tS3Connection_1.append(
									"configuration.staticCredentialConfiguration.secretKey" + " = " + String.valueOf(
											"enc:routine.encryption.key.v1:91XmpxGGP7X+vnwNzfLGLT+hFGGRYYU2izSYNnOe/MKvlXYh8jFh3xY2SdARVIR/ECJ61gYlM3D5r8wzotds5qlfK5M=")
											.substring(0, 4) + "...");
							log4jParamters_Processo_Silver_1_tS3Connection_1.append(" | ");
							log4jParamters_Processo_Silver_1_tS3Connection_1
									.append("configuration.assumeRole" + " = " + "false");
							log4jParamters_Processo_Silver_1_tS3Connection_1.append(" | ");
							log4jParamters_Processo_Silver_1_tS3Connection_1
									.append("configuration.region" + " = " + "DEFAULT");
							log4jParamters_Processo_Silver_1_tS3Connection_1.append(" | ");
							log4jParamters_Processo_Silver_1_tS3Connection_1
									.append("configuration.clientSideEncrypt" + " = " + "false");
							log4jParamters_Processo_Silver_1_tS3Connection_1.append(" | ");
							log4jParamters_Processo_Silver_1_tS3Connection_1
									.append("configuration.useRegionEndpoint" + " = " + "false");
							log4jParamters_Processo_Silver_1_tS3Connection_1.append(" | ");
							log4jParamters_Processo_Silver_1_tS3Connection_1
									.append("configuration.configClient" + " = " + "false");
							log4jParamters_Processo_Silver_1_tS3Connection_1.append(" | ");
							log4jParamters_Processo_Silver_1_tS3Connection_1
									.append("configuration.checkAccessibility" + " = " + "true");
							log4jParamters_Processo_Silver_1_tS3Connection_1.append(" | ");
							log4jParamters_Processo_Silver_1_tS3Connection_1
									.append("configuration.checkMethod" + " = " + "BY_ACCOUNT_OWNER");
							log4jParamters_Processo_Silver_1_tS3Connection_1.append(" | ");
							log4jParamters_Processo_Silver_1_tS3Connection_1
									.append("configuration.enableAccelerate" + " = " + "false");
							log4jParamters_Processo_Silver_1_tS3Connection_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("Processo_Silver_1_tS3Connection_1 - "
										+ (log4jParamters_Processo_Silver_1_tS3Connection_1));
						}
					}
					new BytesLimit65535_Processo_Silver_1_tS3Connection_1().limitLog4jByte();
				}
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_Processo_Silver_1_tS3Connection_1 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter() throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("configuration.credentialProvider", "STATIC_CREDENTIALS");

								component_parameters.put("configuration.staticCredentialConfiguration.accessKey",
										"AKIAUWW4VRZLOLIPY4ZH");

								component_parameters.put("configuration.assumeRole", "false");

								component_parameters.put("configuration.region", "DEFAULT");

								component_parameters.put("configuration.clientSideEncrypt", "false");

								component_parameters.put("configuration.useRegionEndpoint", "false");

								component_parameters.put("configuration.configClient", "false");

								component_parameters.put("configuration.checkAccessibility", "true");

								component_parameters.put("configuration.checkMethod", "BY_ACCOUNT_OWNER");

								component_parameters.put("configuration.enableAccelerate", "false");

							} catch (java.lang.Exception e_Processo_Silver_1_tS3Connection_1) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("Processo_Silver_1_tS3Connection_1", "tS3Connection",
							new ParameterUtil_Processo_Silver_1_tS3Connection_1().getParameter());
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("Processo_Silver_1_tS3Connection_1", "tS3Connection_1", "tS3Connection");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				final org.talend.sdk.component.runtime.manager.ComponentManager mgr_Processo_Silver_1_tS3Connection_1 = org.talend.sdk.component.runtime.manager.ComponentManager
						.instance();
				mgr_Processo_Silver_1_tS3Connection_1.autoDiscoverPluginsIfEmpty(false, true);

				final java.util.Map<String, String> configuration_Processo_Silver_1_tS3Connection_1 = new java.util.HashMap<>();
				final java.util.Map<String, String> registry_metadata_Processo_Silver_1_tS3Connection_1 = new java.util.HashMap<>();

				final class SettingHelper_Processo_Silver_1_tS3Connection_1 {
					final java.util.Map<String, String> configuration;

					SettingHelper_Processo_Silver_1_tS3Connection_1(final java.util.Map<String, String> configuration) {
						this.configuration = configuration;
					}

					void put(String key, String value) {
						if (value != null) {
							configuration.put(key, value);
						}
					}
				}

				final SettingHelper_Processo_Silver_1_tS3Connection_1 s_Processo_Silver_1_tS3Connection_1 = new SettingHelper_Processo_Silver_1_tS3Connection_1(
						configuration_Processo_Silver_1_tS3Connection_1);
				Object dv_Processo_Silver_1_tS3Connection_1;
				java.net.URL mappings_url_Processo_Silver_1_tS3Connection_1 = this.getClass()
						.getResource("/xmlMappings");
				globalMap.put("Processo_Silver_1_tS3Connection_1_MAPPINGS_URL",
						mappings_url_Processo_Silver_1_tS3Connection_1);
				globalMap.putIfAbsent("TALEND_PRODUCT_VERSION", "8.0");
				globalMap.putIfAbsent("TALEND_AWS_TRACKER", "APN/1.0 Talend/8.0 Studio/8.0 (Qlik Talend Cloud)");

				s_Processo_Silver_1_tS3Connection_1.put("configuration.credentialProvider", "STATIC_CREDENTIALS");

				s_Processo_Silver_1_tS3Connection_1.put("configuration.staticCredentialConfiguration.accessKey",
						"AKIAUWW4VRZLOLIPY4ZH");
				s_Processo_Silver_1_tS3Connection_1.put("configuration.staticCredentialConfiguration.secretKey",
						routines.system.PasswordEncryptUtil.decryptPassword(
								"enc:routine.encryption.key.v1:6eiNt9zzW8QIz30xDNE+wgI/PcBsN09+X53CMdhSbLSinJhC34tT1HPh5FPEDFfMVS2Ewqt0wd1UP8q7v9pLt7WcgsU="));

				s_Processo_Silver_1_tS3Connection_1.put("configuration.assumeRole", "false");

				s_Processo_Silver_1_tS3Connection_1.put("configuration.region", "DEFAULT");

				s_Processo_Silver_1_tS3Connection_1.put("configuration.clientSideEncrypt", "false");

				s_Processo_Silver_1_tS3Connection_1.put("configuration.useRegionEndpoint", "false");

				s_Processo_Silver_1_tS3Connection_1.put("configuration.configClient", "false");

				s_Processo_Silver_1_tS3Connection_1.put("configuration.checkAccessibility", "true");

				s_Processo_Silver_1_tS3Connection_1.put("configuration.checkMethod", "BY_ACCOUNT_OWNER");

				s_Processo_Silver_1_tS3Connection_1.put("configuration.enableAccelerate", "false");

				s_Processo_Silver_1_tS3Connection_1.put("configuration.__version", "-1");
				final class SchemaSettingHelper_Processo_Silver_1_tS3Connection_1_1 {

					public void set(final java.util.Map<String, String> configuration) throws java.lang.Exception {
						set_0(configuration);
					}

					public void set_0(final java.util.Map<String, String> configuration) throws java.lang.Exception {
						configuration.put("SCHEMA_FLOW[0]", "CURRENT_BUCKET");
						configuration.put("SCHEMA_FLOW[1]", "CURRENT_KEY");
						configuration.put("SCHEMA_FLOW[2]", "CURRENT_SIZE");
						configuration.put("SCHEMA_FLOW[3]", "CURRENT_LASTMODIFIED");
						configuration.put("SCHEMA_FLOW[4]", "CURRENT_OWNER");
						configuration.put("SCHEMA_FLOW[5]", "CURRENT_OWNER_ID");
						configuration.put("SCHEMA_FLOW[6]", "CURRENT_ETAG");
						configuration.put("SCHEMA_FLOW[7]", "CURRENT_STORAGECLASS");
					}
				}
				new SchemaSettingHelper_Processo_Silver_1_tS3Connection_1_1()
						.set(configuration_Processo_Silver_1_tS3Connection_1);

				mgr_Processo_Silver_1_tS3Connection_1.findPlugin("aws-s3")
						.orElseThrow(() -> new IllegalStateException("Can't find the plugin : aws-s3"))
						.get(org.talend.sdk.component.runtime.manager.ContainerComponentRegistry.class).getServices()
						.stream().forEach(serviceMeta_Processo_Silver_1_tS3Connection_1 -> {
							serviceMeta_Processo_Silver_1_tS3Connection_1.getActions().stream()
									.filter(actionMeta_Processo_Silver_1_tS3Connection_1 -> "create_connection"
											.equals(actionMeta_Processo_Silver_1_tS3Connection_1.getType()))
									.forEach(actionMeta_Processo_Silver_1_tS3Connection_1 -> {
										synchronized (serviceMeta_Processo_Silver_1_tS3Connection_1.getInstance()) {
											org.talend.sdk.component.runtime.di.studio.RuntimeContextInjector
													.injectService(mgr_Processo_Silver_1_tS3Connection_1, "aws-s3",
															new org.talend.sdk.component.api.context.RuntimeContextHolder(
																	"Processo_Silver_1_tS3Connection_1", globalMap));

											Object connnection_Processo_Silver_1_tS3Connection_1 = actionMeta_Processo_Silver_1_tS3Connection_1
													.getInvoker()
													.apply(configuration_Processo_Silver_1_tS3Connection_1);

											globalMap.put("conn_Processo_Silver_1_tS3Connection_1",
													connnection_Processo_Silver_1_tS3Connection_1);

											try {
												configuration_Processo_Silver_1_tS3Connection_1.put(
														"configuration.staticCredentialConfiguration.secretKey",
														routines.system.PasswordEncryptUtil.encryptPassword(
																configuration_Processo_Silver_1_tS3Connection_1.get(
																		"configuration.staticCredentialConfiguration.secretKey")));
											} catch (Exception e) {
												e.printStackTrace();
											}
											globalMap.put("configuration_Processo_Silver_1_tS3Connection_1",
													configuration_Processo_Silver_1_tS3Connection_1);
										}
									});
						});

				/**
				 * [Processo_Silver_1_tS3Connection_1 begin ] stop
				 */

				/**
				 * [Processo_Silver_1_tS3Connection_1 main ] start
				 */

				s(currentComponent = "Processo_Silver_1_tS3Connection_1");

				tos_count_Processo_Silver_1_tS3Connection_1++;

				/**
				 * [Processo_Silver_1_tS3Connection_1 main ] stop
				 */

				/**
				 * [Processo_Silver_1_tS3Connection_1 process_data_begin ] start
				 */

				s(currentComponent = "Processo_Silver_1_tS3Connection_1");

				/**
				 * [Processo_Silver_1_tS3Connection_1 process_data_begin ] stop
				 */

				/**
				 * [Processo_Silver_1_tS3Connection_1 process_data_end ] start
				 */

				s(currentComponent = "Processo_Silver_1_tS3Connection_1");

				/**
				 * [Processo_Silver_1_tS3Connection_1 process_data_end ] stop
				 */

				/**
				 * [Processo_Silver_1_tS3Connection_1 end ] start
				 */

				s(currentComponent = "Processo_Silver_1_tS3Connection_1");

				if (log.isDebugEnabled())
					log.debug("Processo_Silver_1_tS3Connection_1 - " + ("Done."));

				ok_Hash.put("Processo_Silver_1_tS3Connection_1", true);
				end_Hash.put("Processo_Silver_1_tS3Connection_1", System.currentTimeMillis());

				if (execStat) {
					runStat.updateStatOnConnection("Processo_Silver_1_OnComponentOk4", 0, "ok");
				}
				Processo_Silver_1_tS3List_1Process(globalMap);

				/**
				 * [Processo_Silver_1_tS3Connection_1 end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException) && !(e instanceof TDieException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [Processo_Silver_1_tS3Connection_1 finally ] start
				 */

				s(currentComponent = "Processo_Silver_1_tS3Connection_1");

				/**
				 * [Processo_Silver_1_tS3Connection_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("Processo_Silver_1_tS3Connection_1_SUBPROCESS_STATE", 1);
	}

	public void Processo_Silver_1_tS3List_1Process(final java.util.Map<String, Object> globalMap)
			throws TalendException {
		globalMap.put("Processo_Silver_1_tS3List_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("Processo_Silver_1_tS3List_1", "DjJsE5_");

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				/**
				 * [Processo_Silver_1_tS3List_1 begin ] start
				 */

				int NB_ITERATE_Processo_Silver_1_tS3Get_1 = 0; // for statistics

				sh("Processo_Silver_1_tS3List_1");

				s(currentComponent = "Processo_Silver_1_tS3List_1");

				int tos_count_Processo_Silver_1_tS3List_1 = 0;

				if (log.isDebugEnabled())
					log.debug("Processo_Silver_1_tS3List_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_Processo_Silver_1_tS3List_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_Processo_Silver_1_tS3List_1 = new StringBuilder();
							log4jParamters_Processo_Silver_1_tS3List_1.append("Parameters:");
							log4jParamters_Processo_Silver_1_tS3List_1.append("configuration.listAll" + " = " + "true");
							log4jParamters_Processo_Silver_1_tS3List_1.append(" | ");
							log4jParamters_Processo_Silver_1_tS3List_1
									.append("configuration.keyPrefix" + " = " + "\"bronze\"");
							log4jParamters_Processo_Silver_1_tS3List_1.append(" | ");
							log4jParamters_Processo_Silver_1_tS3List_1
									.append("configuration.dieOnError" + " = " + "false");
							log4jParamters_Processo_Silver_1_tS3List_1.append(" | ");
							log4jParamters_Processo_Silver_1_tS3List_1
									.append("configuration.getOwner" + " = " + "false");
							log4jParamters_Processo_Silver_1_tS3List_1.append(" | ");
							log4jParamters_Processo_Silver_1_tS3List_1
									.append("USE_EXISTING_CONNECTION" + " = " + "true");
							log4jParamters_Processo_Silver_1_tS3List_1.append(" | ");
							log4jParamters_Processo_Silver_1_tS3List_1
									.append("CONNECTION" + " = " + "Processo_Silver_1_tS3Connection_1");
							log4jParamters_Processo_Silver_1_tS3List_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("Processo_Silver_1_tS3List_1 - "
										+ (log4jParamters_Processo_Silver_1_tS3List_1));
						}
					}
					new BytesLimit65535_Processo_Silver_1_tS3List_1().limitLog4jByte();
				}
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_Processo_Silver_1_tS3List_1 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter() throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("configuration.listAll", "true");

								component_parameters.put("configuration.keyPrefix", "bronze");

								component_parameters.put("configuration.dieOnError", "false");

								component_parameters.put("configuration.getOwner", "false");
								component_parameters.put("USE_EXISTING_CONNECTION", String.valueOf("true"));
								component_parameters.put("CONNECTION",
										String.valueOf("Processo_Silver_1_tS3Connection_1"));

							} catch (java.lang.Exception e_Processo_Silver_1_tS3List_1) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("Processo_Silver_1_tS3List_1", "S3List",
							new ParameterUtil_Processo_Silver_1_tS3List_1().getParameter());
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("Processo_Silver_1_tS3List_1", "tS3List_1", "S3List");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				final org.talend.sdk.component.runtime.manager.ComponentManager mgr_Processo_Silver_1_tS3List_1 = org.talend.sdk.component.runtime.manager.ComponentManager
						.instance();
				mgr_Processo_Silver_1_tS3List_1.autoDiscoverPluginsIfEmpty(false, true);

				final org.talend.sdk.component.runtime.record.RecordConverters.MappingMetaRegistry registry_Processo_Silver_1_tS3List_1 = new org.talend.sdk.component.runtime.record.RecordConverters.MappingMetaRegistry();

				final java.util.Map<String, String> configuration_Processo_Silver_1_tS3List_1 = new java.util.HashMap<>();
				final java.util.Map<String, String> registry_metadata_Processo_Silver_1_tS3List_1 = new java.util.HashMap<>();

				final class SettingHelper_Processo_Silver_1_tS3List_1 {
					final java.util.Map<String, String> configuration;

					SettingHelper_Processo_Silver_1_tS3List_1(final java.util.Map<String, String> configuration) {
						this.configuration = configuration;
					}

					void put(String key, String value) {
						if (value != null) {
							configuration.put(key, value);
						}
					}
				}

				final SettingHelper_Processo_Silver_1_tS3List_1 s_Processo_Silver_1_tS3List_1 = new SettingHelper_Processo_Silver_1_tS3List_1(
						configuration_Processo_Silver_1_tS3List_1);
				Object dv_Processo_Silver_1_tS3List_1;
				java.net.URL mappings_url_Processo_Silver_1_tS3List_1 = this.getClass().getResource("/xmlMappings");
				globalMap.put("Processo_Silver_1_tS3List_1_MAPPINGS_URL", mappings_url_Processo_Silver_1_tS3List_1);
				globalMap.putIfAbsent("TALEND_PRODUCT_VERSION", "8.0");
				globalMap.putIfAbsent("TALEND_AWS_TRACKER", "APN/1.0 Talend/8.0 Studio/8.0 (Qlik Talend Cloud)");
				final class SchemaSettingHelper_Processo_Silver_1_tS3List_1_1 {

					public void set(final java.util.Map<String, String> configuration) throws java.lang.Exception {
						set_0(configuration);
					}

					public void set_0(final java.util.Map<String, String> configuration) throws java.lang.Exception {
						configuration.put("configuration.schema[0]", "id_assunto");
						configuration.put("configuration.schema[1]", "cod_assunto_cnj");
						configuration.put("configuration.schema[2]", "descricao_assunto");
						configuration.put("configuration.schema[3]", "ramo_direito");
					}
				}
				new SchemaSettingHelper_Processo_Silver_1_tS3List_1_1().set(configuration_Processo_Silver_1_tS3List_1);

				s_Processo_Silver_1_tS3List_1.put("configuration.listAll", "true");

				s_Processo_Silver_1_tS3List_1.put("configuration.keyPrefix", "bronze");

				s_Processo_Silver_1_tS3List_1.put("configuration.dieOnError", "false");

				s_Processo_Silver_1_tS3List_1.put("configuration.getOwner", "false");

				s_Processo_Silver_1_tS3List_1.put("configuration.dataset.__version", "-1");

				s_Processo_Silver_1_tS3List_1.put("configuration.dataset.datastore.__version", "-1");
				final java.util.Map<String, String> config_from_connection_Processo_Silver_1_tS3List_1 = (java.util.Map<String, String>) globalMap
						.get("configuration_Processo_Silver_1_tS3Connection_1");
				final String conn_param_prefix_Processo_Silver_1_tS3List_1 = "configuration.dataset.datastore";
				if (config_from_connection_Processo_Silver_1_tS3List_1 != null
						&& conn_param_prefix_Processo_Silver_1_tS3List_1 != null) {
					final String prefix_Processo_Silver_1_tS3List_1 = config_from_connection_Processo_Silver_1_tS3List_1
							.keySet().stream()
							.filter(key_Processo_Silver_1_tS3List_1 -> key_Processo_Silver_1_tS3List_1
									.endsWith(".__version"))
							.findFirst().map(key_Processo_Silver_1_tS3List_1 -> key_Processo_Silver_1_tS3List_1
									.substring(0, key_Processo_Silver_1_tS3List_1.lastIndexOf(".__version")))
							.orElse(null);

					if (prefix_Processo_Silver_1_tS3List_1 != null) {
						config_from_connection_Processo_Silver_1_tS3List_1.entrySet().stream()
								.filter(entry_Processo_Silver_1_tS3List_1 -> entry_Processo_Silver_1_tS3List_1.getKey()
										.startsWith(prefix_Processo_Silver_1_tS3List_1))
								.forEach(entry_Processo_Silver_1_tS3List_1 -> {
									configuration_Processo_Silver_1_tS3List_1.put(
											entry_Processo_Silver_1_tS3List_1.getKey().replaceFirst(
													prefix_Processo_Silver_1_tS3List_1,
													conn_param_prefix_Processo_Silver_1_tS3List_1),
											entry_Processo_Silver_1_tS3List_1.getValue());
								});
					}
				}

				final org.talend.sdk.component.runtime.input.Mapper tempMapper_Processo_Silver_1_tS3List_1 = mgr_Processo_Silver_1_tS3List_1
						.findMapper("S3", "List", 1, configuration_Processo_Silver_1_tS3List_1)
						.orElseThrow(() -> new IllegalArgumentException("Can't find S3#List"));

				org.talend.sdk.component.runtime.di.studio.RuntimeContextInjector.injectLifecycle(
						tempMapper_Processo_Silver_1_tS3List_1,
						new org.talend.sdk.component.api.context.RuntimeContextHolder("Processo_Silver_1_tS3List_1",
								globalMap));

				final org.talend.sdk.component.runtime.di.studio.ParameterSetter changer_Processo_Silver_1_tS3List_1 = new org.talend.sdk.component.runtime.di.studio.ParameterSetter(
						tempMapper_Processo_Silver_1_tS3List_1);

				final java.util.Map<String, Object> afterVariablesMap_Processo_Silver_1_tS3List_1 = org.talend.sdk.component.runtime.di.studio.AfterVariableExtracter
						.extractAfterVariables(tempMapper_Processo_Silver_1_tS3List_1);

				try {
					Object v_Processo_Silver_1_tS3List_1 = ((org.talend.sdk.component.runtime.base.Delegated) tempMapper_Processo_Silver_1_tS3List_1)
							.getDelegate();
					Object con_Processo_Silver_1_tS3List_1 = globalMap.get("conn_Processo_Silver_1_tS3Connection_1");
					if (con_Processo_Silver_1_tS3List_1 == null) {
						throw new RuntimeException("can't find the connection object");
					}

					Class<?> current_Processo_Silver_1_tS3List_1 = v_Processo_Silver_1_tS3List_1.getClass();
					while (current_Processo_Silver_1_tS3List_1 != null
							&& current_Processo_Silver_1_tS3List_1 != Object.class) {
						java.util.stream.Stream.of(current_Processo_Silver_1_tS3List_1.getDeclaredFields()).filter(
								f_Processo_Silver_1_tS3List_1 -> f_Processo_Silver_1_tS3List_1.isAnnotationPresent(
										org.talend.sdk.component.api.service.connection.Connection.class))
								.forEach(f_Processo_Silver_1_tS3List_1 -> {
									if (!f_Processo_Silver_1_tS3List_1.isAccessible()) {
										f_Processo_Silver_1_tS3List_1.setAccessible(true);
									}
									try {
										f_Processo_Silver_1_tS3List_1.set(v_Processo_Silver_1_tS3List_1,
												con_Processo_Silver_1_tS3List_1);
									} catch (final IllegalAccessException e_Processo_Silver_1_tS3List_1) {
										throw new IllegalStateException(e_Processo_Silver_1_tS3List_1);
									}
								});
						current_Processo_Silver_1_tS3List_1 = current_Processo_Silver_1_tS3List_1.getSuperclass();
					}
				} catch (Exception e_Processo_Silver_1_tS3List_1) {
					throw e_Processo_Silver_1_tS3List_1;
				}

				int nbLineInput_Processo_Silver_1_tS3List_1 = 0;
				globalMap.put("Processo_Silver_1_tS3List_1_NB_LINE", nbLineInput_Processo_Silver_1_tS3List_1);

				org.talend.sdk.component.runtime.di.JobStateAware.init(tempMapper_Processo_Silver_1_tS3List_1,
						globalMap);
				tempMapper_Processo_Silver_1_tS3List_1.start();
				final org.talend.sdk.component.runtime.manager.chain.ChainedMapper mapper_Processo_Silver_1_tS3List_1;
				try {
					final java.util.List<org.talend.sdk.component.runtime.input.Mapper> splitMappers_Processo_Silver_1_tS3List_1 = tempMapper_Processo_Silver_1_tS3List_1
							.split(tempMapper_Processo_Silver_1_tS3List_1.assess());
					mapper_Processo_Silver_1_tS3List_1 = new org.talend.sdk.component.runtime.manager.chain.ChainedMapper(
							tempMapper_Processo_Silver_1_tS3List_1,
							splitMappers_Processo_Silver_1_tS3List_1.iterator());
					mapper_Processo_Silver_1_tS3List_1.start();
					globalMap.put("mapper_Processo_Silver_1_tS3List_1", mapper_Processo_Silver_1_tS3List_1);
				} finally {
					try {
						tempMapper_Processo_Silver_1_tS3List_1.stop();
					} catch (final RuntimeException re) {
						re.printStackTrace();
					}
				}

				final org.talend.sdk.component.runtime.input.Input input_Processo_Silver_1_tS3List_1 = mapper_Processo_Silver_1_tS3List_1
						.create();
				input_Processo_Silver_1_tS3List_1.start();
				globalMap.put("input_Processo_Silver_1_tS3List_1", input_Processo_Silver_1_tS3List_1);

				final javax.json.bind.Jsonb jsonb_Processo_Silver_1_tS3List_1 = (javax.json.bind.Jsonb) mgr_Processo_Silver_1_tS3List_1
						.findPlugin(mapper_Processo_Silver_1_tS3List_1.plugin()).get()
						.get(org.talend.sdk.component.runtime.manager.ComponentManager.AllServices.class).getServices()
						.get(javax.json.bind.Jsonb.class);

				final java.util.Map<Class<?>, Object> servicesMapper_Processo_Silver_1_tS3List_1 = mgr_Processo_Silver_1_tS3List_1
						.findPlugin(mapper_Processo_Silver_1_tS3List_1.plugin()).get()
						.get(org.talend.sdk.component.runtime.manager.ComponentManager.AllServices.class).getServices();
				final javax.json.spi.JsonProvider jsonProvider_Processo_Silver_1_tS3List_1 = javax.json.spi.JsonProvider.class
						.cast(servicesMapper_Processo_Silver_1_tS3List_1.get(javax.json.spi.JsonProvider.class));
				final javax.json.JsonBuilderFactory jsonBuilderFactory_Processo_Silver_1_tS3List_1 = javax.json.JsonBuilderFactory.class
						.cast(servicesMapper_Processo_Silver_1_tS3List_1.get(javax.json.JsonBuilderFactory.class));
				final org.talend.sdk.component.api.service.record.RecordBuilderFactory recordBuilderMapper_Processo_Silver_1_tS3List_1 = org.talend.sdk.component.api.service.record.RecordBuilderFactory.class
						.cast(servicesMapper_Processo_Silver_1_tS3List_1
								.get(org.talend.sdk.component.api.service.record.RecordBuilderFactory.class));
				final org.talend.sdk.component.runtime.record.RecordConverters converters_Processo_Silver_1_tS3List_1 = new org.talend.sdk.component.runtime.record.RecordConverters();

				Object data_Processo_Silver_1_tS3List_1;
				while ((data_Processo_Silver_1_tS3List_1 = input_Processo_Silver_1_tS3List_1.next()) != null) {
					nbLineInput_Processo_Silver_1_tS3List_1++;
					globalMap.put("Processo_Silver_1_tS3List_1_NB_LINE", nbLineInput_Processo_Silver_1_tS3List_1);

					/**
					 * [Processo_Silver_1_tS3List_1 begin ] stop
					 */

					/**
					 * [Processo_Silver_1_tS3List_1 main ] start
					 */

					s(currentComponent = "Processo_Silver_1_tS3List_1");

					tos_count_Processo_Silver_1_tS3List_1++;

					/**
					 * [Processo_Silver_1_tS3List_1 main ] stop
					 */

					/**
					 * [Processo_Silver_1_tS3List_1 process_data_begin ] start
					 */

					s(currentComponent = "Processo_Silver_1_tS3List_1");

					/**
					 * [Processo_Silver_1_tS3List_1 process_data_begin ] stop
					 */

					NB_ITERATE_Processo_Silver_1_tS3Get_1++;

					if (execStat) {
						runStat.updateStatOnConnection("Processo_Silver_1_iterate2", 1,
								"exec" + NB_ITERATE_Processo_Silver_1_tS3Get_1);
						// Thread.sleep(1000);
					}

					/**
					 * [Processo_Silver_1_tS3Get_1 begin ] start
					 */

					sh("Processo_Silver_1_tS3Get_1");

					s(currentComponent = "Processo_Silver_1_tS3Get_1");

					int tos_count_Processo_Silver_1_tS3Get_1 = 0;

					if (log.isDebugEnabled())
						log.debug("Processo_Silver_1_tS3Get_1 - " + ("Start to work."));
					if (log.isDebugEnabled()) {
						class BytesLimit65535_Processo_Silver_1_tS3Get_1 {
							public void limitLog4jByte() throws Exception {
								StringBuilder log4jParamters_Processo_Silver_1_tS3Get_1 = new StringBuilder();
								log4jParamters_Processo_Silver_1_tS3Get_1.append("Parameters:");
								log4jParamters_Processo_Silver_1_tS3Get_1
										.append("configuration.bucket" + " = " + "\"peta-demo-qlik\"");
								log4jParamters_Processo_Silver_1_tS3Get_1.append(" | ");
								log4jParamters_Processo_Silver_1_tS3Get_1.append("configuration.key" + " = "
										+ "((String)globalMap.get(\"Processo_Silver_1_tS3List_1_CURRENT_KEY\"))");
								log4jParamters_Processo_Silver_1_tS3Get_1.append(" | ");
								log4jParamters_Processo_Silver_1_tS3Get_1.append("configuration.file" + " = "
										+ "\"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/assunto_bronze\"");
								log4jParamters_Processo_Silver_1_tS3Get_1.append(" | ");
								log4jParamters_Processo_Silver_1_tS3Get_1
										.append("configuration.dieOnError" + " = " + "false");
								log4jParamters_Processo_Silver_1_tS3Get_1.append(" | ");
								log4jParamters_Processo_Silver_1_tS3Get_1
										.append("configuration.useSelect" + " = " + "false");
								log4jParamters_Processo_Silver_1_tS3Get_1.append(" | ");
								log4jParamters_Processo_Silver_1_tS3Get_1
										.append("configuration.useTempFilesForParallelDownload" + " = " + "false");
								log4jParamters_Processo_Silver_1_tS3Get_1.append(" | ");
								log4jParamters_Processo_Silver_1_tS3Get_1
										.append("USE_EXISTING_CONNECTION" + " = " + "true");
								log4jParamters_Processo_Silver_1_tS3Get_1.append(" | ");
								log4jParamters_Processo_Silver_1_tS3Get_1
										.append("CONNECTION" + " = " + "Processo_Silver_1_tS3Connection_1");
								log4jParamters_Processo_Silver_1_tS3Get_1.append(" | ");
								if (log.isDebugEnabled())
									log.debug("Processo_Silver_1_tS3Get_1 - "
											+ (log4jParamters_Processo_Silver_1_tS3Get_1));
							}
						}
						new BytesLimit65535_Processo_Silver_1_tS3Get_1().limitLog4jByte();
					}
					// QTUP-3575
					if (enableLineage) {
						class ParameterUtil_Processo_Silver_1_tS3Get_1 {

							private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
								java.util.Map<String, String> field = new java.util.HashMap<>();
								field.put("name", values[0]);
								field.put("talend_type", values[1]);
								schema.add(field);
							}

							public java.util.Map<String, String> getParameter() throws Exception {
								java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

								try {

									component_parameters.put("configuration.bucket", "peta-demo-qlik");

									component_parameters.put("configuration.key", String.valueOf(
											((String) globalMap.get("Processo_Silver_1_tS3List_1_CURRENT_KEY"))));

									component_parameters.put("configuration.file",
											"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/assunto_bronze");

									component_parameters.put("configuration.dieOnError", "false");

									component_parameters.put("configuration.useSelect", "false");

									component_parameters.put("configuration.useTempFilesForParallelDownload", "false");
									component_parameters.put("USE_EXISTING_CONNECTION", String.valueOf("true"));
									component_parameters.put("CONNECTION",
											String.valueOf("Processo_Silver_1_tS3Connection_1"));

								} catch (java.lang.Exception e_Processo_Silver_1_tS3Get_1) {
									// do nothing
								}

								return component_parameters;
							}
						}

						talendJobLog.addComponentParameterMessage("Processo_Silver_1_tS3Get_1", "S3Get",
								new ParameterUtil_Processo_Silver_1_tS3Get_1().getParameter());
						talendJobLogProcess(globalMap);
						s(currentComponent);
					}
					// QTUP-3575

					if (enableLogStash) {
						talendJobLog.addCM("Processo_Silver_1_tS3Get_1", "tS3Get_1", "S3Get");
						talendJobLogProcess(globalMap);
						s(currentComponent);
					}

					final org.talend.sdk.component.runtime.manager.ComponentManager mgr_Processo_Silver_1_tS3Get_1 = org.talend.sdk.component.runtime.manager.ComponentManager
							.instance();
					mgr_Processo_Silver_1_tS3Get_1.autoDiscoverPluginsIfEmpty(false, true);

					final java.util.Map<String, String> configuration_Processo_Silver_1_tS3Get_1 = new java.util.HashMap<>();
					final java.util.Map<String, String> registry_metadata_Processo_Silver_1_tS3Get_1 = new java.util.HashMap<>();

					final class SettingHelper_Processo_Silver_1_tS3Get_1 {
						final java.util.Map<String, String> configuration;

						SettingHelper_Processo_Silver_1_tS3Get_1(final java.util.Map<String, String> configuration) {
							this.configuration = configuration;
						}

						void put(String key, String value) {
							if (value != null) {
								configuration.put(key, value);
							}
						}
					}

					final SettingHelper_Processo_Silver_1_tS3Get_1 s_Processo_Silver_1_tS3Get_1 = new SettingHelper_Processo_Silver_1_tS3Get_1(
							configuration_Processo_Silver_1_tS3Get_1);
					Object dv_Processo_Silver_1_tS3Get_1;
					java.net.URL mappings_url_Processo_Silver_1_tS3Get_1 = this.getClass().getResource("/xmlMappings");
					globalMap.put("Processo_Silver_1_tS3Get_1_MAPPINGS_URL", mappings_url_Processo_Silver_1_tS3Get_1);
					globalMap.putIfAbsent("TALEND_PRODUCT_VERSION", "8.0");
					globalMap.putIfAbsent("TALEND_AWS_TRACKER", "APN/1.0 Talend/8.0 Studio/8.0 (Qlik Talend Cloud)");

					s_Processo_Silver_1_tS3Get_1.put("configuration.bucket", "peta-demo-qlik");

					dv_Processo_Silver_1_tS3Get_1 = ((String) globalMap.get("Processo_Silver_1_tS3List_1_CURRENT_KEY"));
					if (dv_Processo_Silver_1_tS3Get_1 instanceof java.io.InputStream) {
						s_Processo_Silver_1_tS3Get_1.put("configuration.key",
								"((String)globalMap.get(\"Processo_Silver_1_tS3List_1_CURRENT_KEY\"))");
					} else {
						s_Processo_Silver_1_tS3Get_1.put("configuration.key",
								String.valueOf(((String) globalMap.get("Processo_Silver_1_tS3List_1_CURRENT_KEY"))));
					}

					s_Processo_Silver_1_tS3Get_1.put("configuration.file",
							"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/assunto_bronze");

					s_Processo_Silver_1_tS3Get_1.put("configuration.dieOnError", "false");

					s_Processo_Silver_1_tS3Get_1.put("configuration.useSelect", "false");

					s_Processo_Silver_1_tS3Get_1.put("configuration.useTempFilesForParallelDownload", "false");

					s_Processo_Silver_1_tS3Get_1.put("configuration.dataset.__version", "-1");

					s_Processo_Silver_1_tS3Get_1.put("configuration.dataset.datastore.__version", "-1");
					final class SchemaSettingHelper_Processo_Silver_1_tS3Get_1_1 {

						public void set(final java.util.Map<String, String> configuration) throws java.lang.Exception {
						}
					}
					new SchemaSettingHelper_Processo_Silver_1_tS3Get_1_1()
							.set(configuration_Processo_Silver_1_tS3Get_1);
					final class SchemaSettingHelper_Processo_Silver_1_tS3Get_1_2 {

						public void set(final java.util.Map<String, String> configuration) throws java.lang.Exception {
						}
					}
					new SchemaSettingHelper_Processo_Silver_1_tS3Get_1_2()
							.set(configuration_Processo_Silver_1_tS3Get_1);
					final java.util.Map<String, String> config_from_connection_Processo_Silver_1_tS3Get_1 = (java.util.Map<String, String>) globalMap
							.get("configuration_Processo_Silver_1_tS3Connection_1");
					final String conn_param_prefix_Processo_Silver_1_tS3Get_1 = "configuration.dataset.datastore";
					if (config_from_connection_Processo_Silver_1_tS3Get_1 != null
							&& conn_param_prefix_Processo_Silver_1_tS3Get_1 != null) {
						final String prefix_Processo_Silver_1_tS3Get_1 = config_from_connection_Processo_Silver_1_tS3Get_1
								.keySet().stream()
								.filter(key_Processo_Silver_1_tS3Get_1 -> key_Processo_Silver_1_tS3Get_1
										.endsWith(".__version"))
								.findFirst().map(key_Processo_Silver_1_tS3Get_1 -> key_Processo_Silver_1_tS3Get_1
										.substring(0, key_Processo_Silver_1_tS3Get_1.lastIndexOf(".__version")))
								.orElse(null);

						if (prefix_Processo_Silver_1_tS3Get_1 != null) {
							config_from_connection_Processo_Silver_1_tS3Get_1.entrySet().stream()
									.filter(entry_Processo_Silver_1_tS3Get_1 -> entry_Processo_Silver_1_tS3Get_1
											.getKey().startsWith(prefix_Processo_Silver_1_tS3Get_1))
									.forEach(entry_Processo_Silver_1_tS3Get_1 -> {
										configuration_Processo_Silver_1_tS3Get_1.put(
												entry_Processo_Silver_1_tS3Get_1.getKey().replaceFirst(
														prefix_Processo_Silver_1_tS3Get_1,
														conn_param_prefix_Processo_Silver_1_tS3Get_1),
												entry_Processo_Silver_1_tS3Get_1.getValue());
									});
						}
					}

					final org.talend.sdk.component.runtime.standalone.DriverRunner standalone_Processo_Silver_1_tS3Get_1 = mgr_Processo_Silver_1_tS3Get_1
							.findDriverRunner("S3", "Get", 1, configuration_Processo_Silver_1_tS3Get_1)
							.orElseThrow(() -> new IllegalArgumentException("Can't find S3#Get"));

					org.talend.sdk.component.runtime.di.studio.RuntimeContextInjector.injectLifecycle(
							standalone_Processo_Silver_1_tS3Get_1,
							new org.talend.sdk.component.api.context.RuntimeContextHolder("Processo_Silver_1_tS3Get_1",
									globalMap));

					try {
						java.lang.reflect.Field field_Processo_Silver_1_tS3Get_1 = standalone_Processo_Silver_1_tS3Get_1
								.getClass().getSuperclass().getDeclaredField("delegate");
						if (!field_Processo_Silver_1_tS3Get_1.isAccessible()) {
							field_Processo_Silver_1_tS3Get_1.setAccessible(true);
						}
						Object v_Processo_Silver_1_tS3Get_1 = field_Processo_Silver_1_tS3Get_1
								.get(standalone_Processo_Silver_1_tS3Get_1);
						Object con_Processo_Silver_1_tS3Get_1 = globalMap.get("conn_Processo_Silver_1_tS3Connection_1");
						if (con_Processo_Silver_1_tS3Get_1 == null) {
							throw new RuntimeException("can't find the connection object");
						}

						Class<?> current_Processo_Silver_1_tS3Get_1 = v_Processo_Silver_1_tS3Get_1.getClass();
						while (current_Processo_Silver_1_tS3Get_1 != null
								&& current_Processo_Silver_1_tS3Get_1 != Object.class) {
							java.util.stream.Stream.of(current_Processo_Silver_1_tS3Get_1.getDeclaredFields()).filter(
									f_Processo_Silver_1_tS3Get_1 -> f_Processo_Silver_1_tS3Get_1.isAnnotationPresent(
											org.talend.sdk.component.api.service.connection.Connection.class))
									.forEach(f_Processo_Silver_1_tS3Get_1 -> {
										if (!f_Processo_Silver_1_tS3Get_1.isAccessible()) {
											f_Processo_Silver_1_tS3Get_1.setAccessible(true);
										}
										try {
											f_Processo_Silver_1_tS3Get_1.set(v_Processo_Silver_1_tS3Get_1,
													con_Processo_Silver_1_tS3Get_1);
										} catch (final IllegalAccessException e_Processo_Silver_1_tS3Get_1) {
											throw new IllegalStateException(e_Processo_Silver_1_tS3Get_1);
										}
									});
							current_Processo_Silver_1_tS3Get_1 = current_Processo_Silver_1_tS3Get_1.getSuperclass();
						}
					} catch (Exception e_Processo_Silver_1_tS3Get_1) {
						throw e_Processo_Silver_1_tS3Get_1;
					}

					standalone_Processo_Silver_1_tS3Get_1.start();
					globalMap.put("standalone_Processo_Silver_1_tS3Get_1", standalone_Processo_Silver_1_tS3Get_1);

					standalone_Processo_Silver_1_tS3Get_1.runAtDriver();
//Standalone begin stub

					/**
					 * [Processo_Silver_1_tS3Get_1 begin ] stop
					 */

					/**
					 * [Processo_Silver_1_tS3Get_1 main ] start
					 */

					s(currentComponent = "Processo_Silver_1_tS3Get_1");

					tos_count_Processo_Silver_1_tS3Get_1++;

					/**
					 * [Processo_Silver_1_tS3Get_1 main ] stop
					 */

					/**
					 * [Processo_Silver_1_tS3Get_1 process_data_begin ] start
					 */

					s(currentComponent = "Processo_Silver_1_tS3Get_1");

					/**
					 * [Processo_Silver_1_tS3Get_1 process_data_begin ] stop
					 */

					/**
					 * [Processo_Silver_1_tS3Get_1 process_data_end ] start
					 */

					s(currentComponent = "Processo_Silver_1_tS3Get_1");

					/**
					 * [Processo_Silver_1_tS3Get_1 process_data_end ] stop
					 */

					/**
					 * [Processo_Silver_1_tS3Get_1 end ] start
					 */

					s(currentComponent = "Processo_Silver_1_tS3Get_1");

					if (standalone_Processo_Silver_1_tS3Get_1 != null) {
						standalone_Processo_Silver_1_tS3Get_1.stop();
					}

					globalMap.remove("standalone_Processo_Silver_1_tS3Get_1");

					if (log.isDebugEnabled())
						log.debug("Processo_Silver_1_tS3Get_1 - " + ("Done."));

					ok_Hash.put("Processo_Silver_1_tS3Get_1", true);
					end_Hash.put("Processo_Silver_1_tS3Get_1", System.currentTimeMillis());

					/**
					 * [Processo_Silver_1_tS3Get_1 end ] stop
					 */

					if (execStat) {
						runStat.updateStatOnConnection("Processo_Silver_1_iterate2", 2,
								"exec" + NB_ITERATE_Processo_Silver_1_tS3Get_1);
					}

					/**
					 * [Processo_Silver_1_tS3List_1 process_data_end ] start
					 */

					s(currentComponent = "Processo_Silver_1_tS3List_1");

					/**
					 * [Processo_Silver_1_tS3List_1 process_data_end ] stop
					 */

					/**
					 * [Processo_Silver_1_tS3List_1 end ] start
					 */

					s(currentComponent = "Processo_Silver_1_tS3List_1");

				} // while

				// extract after variables from the processor map and put to after variables map
				// of job
				for (java.util.Map.Entry<String, Object> entry_Processo_Silver_1_tS3List_1 : afterVariablesMap_Processo_Silver_1_tS3List_1
						.entrySet()) {
					globalMap.put("Processo_Silver_1_tS3List_1_" + entry_Processo_Silver_1_tS3List_1.getKey(),
							entry_Processo_Silver_1_tS3List_1.getValue());
				}

				boolean swallowNextException_Processo_Silver_1_tS3List_1 = false;
				try {
					if (input_Processo_Silver_1_tS3List_1 != null) {
						input_Processo_Silver_1_tS3List_1.stop();
					}
				} catch (final RuntimeException re) {
					swallowNextException_Processo_Silver_1_tS3List_1 = true;
					throw re;
				} finally {
					try {
						if (mapper_Processo_Silver_1_tS3List_1 != null) {
							mapper_Processo_Silver_1_tS3List_1.stop();
						}
					} catch (final RuntimeException re) {
						if (!swallowNextException_Processo_Silver_1_tS3List_1) {
							throw re;
						}
					}
				}
				globalMap.put("Processo_Silver_1_tS3List_1_NB_LINE", nbLineInput_Processo_Silver_1_tS3List_1);
				globalMap.remove("mapper_Processo_Silver_1_tS3List_1");
				globalMap.remove("input_Processo_Silver_1_tS3List_1");

				if (log.isDebugEnabled())
					log.debug("Processo_Silver_1_tS3List_1 - " + ("Done."));

				ok_Hash.put("Processo_Silver_1_tS3List_1", true);
				end_Hash.put("Processo_Silver_1_tS3List_1", System.currentTimeMillis());

				/**
				 * [Processo_Silver_1_tS3List_1 end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException) && !(e instanceof TDieException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [Processo_Silver_1_tS3List_1 finally ] start
				 */

				s(currentComponent = "Processo_Silver_1_tS3List_1");

				final org.talend.sdk.component.runtime.input.Mapper mapper_Processo_Silver_1_tS3List_1 = org.talend.sdk.component.runtime.input.Mapper.class
						.cast(globalMap.remove("mapper_Processo_Silver_1_tS3List_1"));
				final org.talend.sdk.component.runtime.input.Input input_Processo_Silver_1_tS3List_1 = org.talend.sdk.component.runtime.input.Input.class
						.cast(globalMap.remove("input_Processo_Silver_1_tS3List_1"));

				boolean swallowNextException_Processo_Silver_1_tS3List_1 = false;
				try {
					if (input_Processo_Silver_1_tS3List_1 != null) {
						input_Processo_Silver_1_tS3List_1.stop();
					}
				} catch (final RuntimeException re) {
					swallowNextException_Processo_Silver_1_tS3List_1 = true;
					throw new TalendException(re, currentComponent, cLabel, globalMap);
				} finally {
					try {
						if (mapper_Processo_Silver_1_tS3List_1 != null) {
							mapper_Processo_Silver_1_tS3List_1.stop();
						}
					} catch (final RuntimeException re) {
						if (!swallowNextException_Processo_Silver_1_tS3List_1) {
							throw new TalendException(re, currentComponent, cLabel, globalMap);
						}
					}
				}

				/**
				 * [Processo_Silver_1_tS3List_1 finally ] stop
				 */

				/**
				 * [Processo_Silver_1_tS3Get_1 finally ] start
				 */

				s(currentComponent = "Processo_Silver_1_tS3Get_1");

				final org.talend.sdk.component.runtime.standalone.DriverRunner standalone_Processo_Silver_1_tS3Get_1 = org.talend.sdk.component.runtime.standalone.DriverRunner.class
						.cast(globalMap.remove("standalone_Processo_Silver_1_tS3Get_1"));
				try {
					if (standalone_Processo_Silver_1_tS3Get_1 != null) {
						standalone_Processo_Silver_1_tS3Get_1.stop();
					}
				} catch (final RuntimeException re) {
					throw new TalendException(re, currentComponent, cLabel, globalMap);
				}

				/**
				 * [Processo_Silver_1_tS3Get_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("Processo_Silver_1_tS3List_1_SUBPROCESS_STATE", 1);
	}

	public static class Processos_Ingestao_Bronze_1_row1Struct
			implements routines.system.IPersistableRow<Processos_Ingestao_Bronze_1_row1Struct> {
		final static byte[] commonByteArrayLock_DEMOTRF5_TRF5 = new byte[0];
		static byte[] commonByteArray_DEMOTRF5_TRF5 = new byte[0];
		protected static final int DEFAULT_HASHCODE = 1;
		protected static final int PRIME = 31;
		protected int hashCode = DEFAULT_HASHCODE;
		public boolean hashCodeDirty = true;

		public String loopKey;

		public int id_processo;

		public int getId_processo() {
			return this.id_processo;
		}

		public Boolean id_processoIsNullable() {
			return false;
		}

		public Boolean id_processoIsKey() {
			return true;
		}

		public Integer id_processoLength() {
			return 10;
		}

		public Integer id_processoPrecision() {
			return 0;
		}

		public String id_processoDefault() {

			return "nextval('processos_judiciais_id_processo_seq'::regclass)";

		}

		public String id_processoComment() {

			return "";

		}

		public String id_processoPattern() {

			return "";

		}

		public String id_processoOriginalDbColumnName() {

			return "id_processo";

		}

		public String num_processo;

		public String getNum_processo() {
			return this.num_processo;
		}

		public Boolean num_processoIsNullable() {
			return false;
		}

		public Boolean num_processoIsKey() {
			return false;
		}

		public Integer num_processoLength() {
			return 25;
		}

		public Integer num_processoPrecision() {
			return 0;
		}

		public String num_processoDefault() {

			return null;

		}

		public String num_processoComment() {

			return "";

		}

		public String num_processoPattern() {

			return "";

		}

		public String num_processoOriginalDbColumnName() {

			return "num_processo";

		}

		public java.util.Date data_abertura;

		public java.util.Date getData_abertura() {
			return this.data_abertura;
		}

		public Boolean data_aberturaIsNullable() {
			return false;
		}

		public Boolean data_aberturaIsKey() {
			return false;
		}

		public Integer data_aberturaLength() {
			return 13;
		}

		public Integer data_aberturaPrecision() {
			return 0;
		}

		public String data_aberturaDefault() {

			return null;

		}

		public String data_aberturaComment() {

			return "";

		}

		public String data_aberturaPattern() {

			return "dd-MM-yyyy";

		}

		public String data_aberturaOriginalDbColumnName() {

			return "data_abertura";

		}

		public String classe;

		public String getClasse() {
			return this.classe;
		}

		public Boolean classeIsNullable() {
			return true;
		}

		public Boolean classeIsKey() {
			return false;
		}

		public Integer classeLength() {
			return 100;
		}

		public Integer classePrecision() {
			return 0;
		}

		public String classeDefault() {

			return null;

		}

		public String classeComment() {

			return "";

		}

		public String classePattern() {

			return "";

		}

		public String classeOriginalDbColumnName() {

			return "classe";

		}

		public String tribunal;

		public String getTribunal() {
			return this.tribunal;
		}

		public Boolean tribunalIsNullable() {
			return true;
		}

		public Boolean tribunalIsKey() {
			return false;
		}

		public Integer tribunalLength() {
			return 10;
		}

		public Integer tribunalPrecision() {
			return 0;
		}

		public String tribunalDefault() {

			return "'TRF5'::character varying'";

		}

		public String tribunalComment() {

			return "";

		}

		public String tribunalPattern() {

			return "";

		}

		public String tribunalOriginalDbColumnName() {

			return "tribunal";

		}

		@Override
		public int hashCode() {
			if (this.hashCodeDirty) {
				final int prime = PRIME;
				int result = DEFAULT_HASHCODE;

				result = prime * result + (int) this.id_processo;

				this.hashCode = result;
				this.hashCodeDirty = false;
			}
			return this.hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Processos_Ingestao_Bronze_1_row1Struct other = (Processos_Ingestao_Bronze_1_row1Struct) obj;

			if (this.id_processo != other.id_processo)
				return false;

			return true;
		}

		public void copyDataTo(Processos_Ingestao_Bronze_1_row1Struct other) {

			other.id_processo = this.id_processo;
			other.num_processo = this.num_processo;
			other.data_abertura = this.data_abertura;
			other.classe = this.classe;
			other.tribunal = this.tribunal;

		}

		public void copyKeysDataTo(Processos_Ingestao_Bronze_1_row1Struct other) {

			other.id_processo = this.id_processo;

		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_DEMOTRF5_TRF5.length) {
					if (length < 1024 && commonByteArray_DEMOTRF5_TRF5.length == 0) {
						commonByteArray_DEMOTRF5_TRF5 = new byte[1024];
					} else {
						commonByteArray_DEMOTRF5_TRF5 = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_DEMOTRF5_TRF5, 0, length);
				strReturn = new String(commonByteArray_DEMOTRF5_TRF5, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			String strReturn = null;
			int length = 0;
			length = unmarshaller.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_DEMOTRF5_TRF5.length) {
					if (length < 1024 && commonByteArray_DEMOTRF5_TRF5.length == 0) {
						commonByteArray_DEMOTRF5_TRF5 = new byte[1024];
					} else {
						commonByteArray_DEMOTRF5_TRF5 = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_DEMOTRF5_TRF5, 0, length);
				strReturn = new String(commonByteArray_DEMOTRF5_TRF5, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos) throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (str == null) {
				marshaller.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				marshaller.writeInt(byteArray.length);
				marshaller.write(byteArray);
			}
		}

		private java.util.Date readDate(ObjectInputStream dis) throws IOException {
			java.util.Date dateReturn = null;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				dateReturn = null;
			} else {
				dateReturn = new Date(dis.readLong());
			}
			return dateReturn;
		}

		private java.util.Date readDate(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			java.util.Date dateReturn = null;
			int length = 0;
			length = unmarshaller.readByte();
			if (length == -1) {
				dateReturn = null;
			} else {
				dateReturn = new Date(unmarshaller.readLong());
			}
			return dateReturn;
		}

		private void writeDate(java.util.Date date1, ObjectOutputStream dos) throws IOException {
			if (date1 == null) {
				dos.writeByte(-1);
			} else {
				dos.writeByte(0);
				dos.writeLong(date1.getTime());
			}
		}

		private void writeDate(java.util.Date date1, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (date1 == null) {
				marshaller.writeByte(-1);
			} else {
				marshaller.writeByte(0);
				marshaller.writeLong(date1.getTime());
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_DEMOTRF5_TRF5) {

				try {

					int length = 0;

					this.id_processo = dis.readInt();

					this.num_processo = readString(dis);

					this.data_abertura = readDate(dis);

					this.classe = readString(dis);

					this.tribunal = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_DEMOTRF5_TRF5) {

				try {

					int length = 0;

					this.id_processo = dis.readInt();

					this.num_processo = readString(dis);

					this.data_abertura = readDate(dis);

					this.classe = readString(dis);

					this.tribunal = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// int

				dos.writeInt(this.id_processo);

				// String

				writeString(this.num_processo, dos);

				// java.util.Date

				writeDate(this.data_abertura, dos);

				// String

				writeString(this.classe, dos);

				// String

				writeString(this.tribunal, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// int

				dos.writeInt(this.id_processo);

				// String

				writeString(this.num_processo, dos);

				// java.util.Date

				writeDate(this.data_abertura, dos);

				// String

				writeString(this.classe, dos);

				// String

				writeString(this.tribunal, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("id_processo=" + String.valueOf(id_processo));
			sb.append(",num_processo=" + num_processo);
			sb.append(",data_abertura=" + String.valueOf(data_abertura));
			sb.append(",classe=" + classe);
			sb.append(",tribunal=" + tribunal);
			sb.append("]");

			return sb.toString();
		}

		public String toLogString() {
			StringBuilder sb = new StringBuilder();

			sb.append(id_processo);

			sb.append("|");

			if (num_processo == null) {
				sb.append("<null>");
			} else {
				sb.append(num_processo);
			}

			sb.append("|");

			if (data_abertura == null) {
				sb.append("<null>");
			} else {
				sb.append(data_abertura);
			}

			sb.append("|");

			if (classe == null) {
				sb.append("<null>");
			} else {
				sb.append(classe);
			}

			sb.append("|");

			if (tribunal == null) {
				sb.append("<null>");
			} else {
				sb.append(tribunal);
			}

			sb.append("|");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(Processos_Ingestao_Bronze_1_row1Struct other) {

			int returnValue = -1;

			returnValue = checkNullsAndCompare(this.id_processo, other.id_processo);
			if (returnValue != 0) {
				return returnValue;
			}

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(), object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public void Processos_Ingestao_Bronze_1_tDBInput_1Process(final java.util.Map<String, Object> globalMap)
			throws TalendException {
		globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("Processos_Ingestao_Bronze_1_tDBInput_1", "zPneM4_");

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				Processos_Ingestao_Bronze_1_row1Struct Processos_Ingestao_Bronze_1_row1 = new Processos_Ingestao_Bronze_1_row1Struct();

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_1 begin ] start
				 */

				sh("Processos_Ingestao_Bronze_1_tFileOutputParquet_1");

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_1");

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0,
						"Processos_Ingestao_Bronze_1_row1");

				int tos_count_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = 0;

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = new StringBuilder();
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.append("Parameters:");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.append("FILENAME" + " = "
									+ "\"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/processo_bronze\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
									.append("FILE_ACTION" + " = " + "OVERWRITE");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
									.append("COMPRESSION" + " = " + "\"UNCOMPRESSED\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
									.append("USE_EXTERNAL_HADOOP_DEPS" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
									.append("ROW_GROUP_SIZE" + " = " + "134217728");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
									.append("PAGE_SIZE" + " = " + "1048576");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_1 - "
										+ (log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_1));
						}
					}
					new BytesLimit65535_Processos_Ingestao_Bronze_1_tFileOutputParquet_1().limitLog4jByte();
				}
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter(
								final Processos_Ingestao_Bronze_1_row1Struct Processos_Ingestao_Bronze_1_row1)
								throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("FILENAME", String.valueOf(
										"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/processo_bronze"));
								component_parameters.put("FILE_ACTION", String.valueOf("OVERWRITE"));
								component_parameters.put("COMPRESSION", String.valueOf("UNCOMPRESSED"));
								component_parameters.put("USE_EXTERNAL_HADOOP_DEPS", String.valueOf("false"));
								component_parameters.put("ROW_GROUP_SIZE", String.valueOf("134217728"));
								component_parameters.put("PAGE_SIZE", String.valueOf("1048576"));

							} catch (java.lang.Exception e_Processos_Ingestao_Bronze_1_tFileOutputParquet_1) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("Processos_Ingestao_Bronze_1_tFileOutputParquet_1",
							"tFileOutputParquet", new ParameterUtil_Processos_Ingestao_Bronze_1_tFileOutputParquet_1()
									.getParameter(Processos_Ingestao_Bronze_1_row1));
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("Processos_Ingestao_Bronze_1_tFileOutputParquet_1", "tFileOutputParquet_1",
							"tFileOutputParquet");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				int nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = 0;
				org.apache.parquet.schema.Types.MessageTypeBuilder schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = org.apache.parquet.schema.Types
						.buildMessage();
				org.apache.parquet.schema.MessageType messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = null;
				org.talend.parquet.data.simple.SimpleGroupFactory factory_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = null;
				org.apache.parquet.hadoop.ParquetWriter<org.talend.parquet.data.Group> writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = null;

				String filePath_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = "C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/processo_bronze";
				globalMap.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_1_FILE_PATH",
						filePath_Processos_Ingestao_Bronze_1_tFileOutputParquet_1);
				org.apache.hadoop.conf.Configuration config_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = new org.apache.hadoop.conf.Configuration();
				config_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.set("fs.file.impl",
						org.apache.hadoop.fs.LocalFileSystem.class.getName());
				org.apache.hadoop.fs.Path path_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = new org.apache.hadoop.fs.Path(
						filePath_Processos_Ingestao_Bronze_1_tFileOutputParquet_1);
				// CRC file path
				String crcName_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = "."
						+ path_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.getName() + ".crc";
				org.apache.hadoop.fs.Path crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = new org.apache.hadoop.fs.Path(
						path_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.getParent(),
						crcName_Processos_Ingestao_Bronze_1_tFileOutputParquet_1);
				String compressName_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = "UNCOMPRESSED";
				int rowGroupSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = 134217728;
				int pageSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = 1048576;
				java.util.Map<String, org.talend.parquet.data.simple.SimpleGroupFactory> cachedFactory_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = new java.util.HashMap<>();
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("id_processo", false,
								"INT32", "INT_32"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("num_processo", false,
								"BINARY", "UTF8"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("data_abertura",
								false, "INT64", "TIMESTAMP_MILLIS"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("classe", true,
								"BINARY", "UTF8"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("tribunal", true,
								"BINARY", "UTF8"));
				messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
						.named("Schema");

				org.talend.parquet.hadoop.TalendGroupWriteSupport.setSchema(
						messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_1,
						config_Processos_Ingestao_Bronze_1_tFileOutputParquet_1);
				factory_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = new org.talend.parquet.data.simple.SimpleGroupFactory(
						messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_1);
				org.apache.parquet.hadoop.ParquetWriter.Builder<org.talend.parquet.data.Group, org.talend.parquet.hadoop.TalendParquetWriter.Builder> builder_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = org.talend.parquet.hadoop.TalendParquetWriter
						.builder(org.apache.parquet.hadoop.util.HadoopOutputFile.fromPath(
								path_Processos_Ingestao_Bronze_1_tFileOutputParquet_1,
								config_Processos_Ingestao_Bronze_1_tFileOutputParquet_1));
				builder_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
						.withWriteMode(org.apache.parquet.hadoop.ParquetFileWriter.Mode.OVERWRITE)
						.withCompressionCodec(org.apache.parquet.hadoop.metadata.CompressionCodecName
								.fromConf(compressName_Processos_Ingestao_Bronze_1_tFileOutputParquet_1))
						.withRowGroupSize(rowGroupSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_1)
						.withPageSize(pageSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_1)
						.withConf(config_Processos_Ingestao_Bronze_1_tFileOutputParquet_1);

				writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = builder_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
						.build();

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_1 begin ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_1 begin ] start
				 */

				sh("Processos_Ingestao_Bronze_1_tDBInput_1");

				s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_1");

				int tos_count_Processos_Ingestao_Bronze_1_tDBInput_1 = 0;

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tDBInput_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_Processos_Ingestao_Bronze_1_tDBInput_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1 = new StringBuilder();
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append("Parameters:");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1
									.append("USE_EXISTING_CONNECTION" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append("DB_VERSION" + " = " + "V9_X");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append("HOST" + " = "
									+ "\"peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append("PORT" + " = " + "\"5432\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1
									.append("DBNAME" + " = " + "\"qlik_demo\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1
									.append("SCHEMA_DB" + " = " + "\"public\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1
									.append("USER" + " = " + "\"peta_qlik\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append("PASS" + " = "
									+ String.valueOf(
											"enc:routine.encryption.key.v1:9wlRW7GPPwV2r0lkpb5hYGw4Hdx2+Yj7gFzG+0HH6+DU/wVugA==")
											.substring(0, 4)
									+ "...");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append("QUERYSTORE" + " = " + "\"\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append("QUERY" + " = "
									+ "\"SELECT    \\\"public\\\".\\\"processos\\\".\\\"id_processo\\\",    \\\"public\\\".\\\"processos\\\".\\\"numero_processo\\\",    \\\"public\\\".\\\"processos\\\".\\\"data_ajuizamento\\\",    \\\"public\\\".\\\"processos\\\".\\\"id_classe\\\",     \\\"public\\\".\\\"processos\\\".\\\"id_assunto\\\",     \\\"public\\\".\\\"processos\\\".\\\"id_magistrado\\\",     \\\"public\\\".\\\"processos\\\".\\\"valor_causa\\\",    \\\"public\\\".\\\"processos\\\".\\\"tempo_processo_dias\\\",    \\\"public\\\".\\\"processos\\\".\\\"id_tribunal\\\"  FROM \\\"public\\\".\\\"processos\\\"\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1
									.append("SPECIFY_DATASOURCE_ALIAS" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1
									.append("PROPERTIES" + " = " + "\"processos_judiciais\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1
									.append("USE_CURSOR" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1
									.append("TRIM_ALL_COLUMN" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(
									"TRIM_COLUMN" + " = " + "[{TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("id_processo")
											+ "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("num_processo")
											+ "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("data_abertura")
											+ "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("classe") + "}, {TRIM="
											+ ("false") + ", SCHEMA_COLUMN=" + ("tribunal") + "}]");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1
									.append("UNIFIED_COMPONENTS" + " = " + "tPostgresqlInput");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("Processos_Ingestao_Bronze_1_tDBInput_1 - "
										+ (log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_1));
						}
					}
					new BytesLimit65535_Processos_Ingestao_Bronze_1_tDBInput_1().limitLog4jByte();
				}
				boolean init_Processos_Ingestao_Bronze_1_tDBInput_1_0 = true;
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_Processos_Ingestao_Bronze_1_tDBInput_1 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter() throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("USE_EXISTING_CONNECTION", String.valueOf("false"));
								component_parameters.put("DB_VERSION", String.valueOf("V9_X"));
								component_parameters.put("HOST", String
										.valueOf("peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com"));
								component_parameters.put("PORT", String.valueOf("5432"));
								component_parameters.put("DBNAME", String.valueOf("qlik_demo"));
								component_parameters.put("SCHEMA_DB", String.valueOf("public"));
								component_parameters.put("USER", String.valueOf("peta_qlik"));
								component_parameters.put("QUERYSTORE", String.valueOf(""));
								component_parameters.put("QUERY", String.valueOf(new StringBuilder().append(
										"SELECT \n  \"public\".\"processos\".\"id_processo\", \n  \"public\".\"processos\".\"numero_processo\", \n  \"public\".\"pr"
												+ "ocessos\".\"data_ajuizamento\", \n  \"public\".\"processos\".\"id_classe\", \n  \"public\".\"processos\".\"id_assunto\", "
												+ "\n  \"public\".\"processos\".\"id_magistrado\", \n  \"public\".\"processos\".\"valor_causa\", \n \"public\".\"processos"
												+ "\".\"tempo_processo_dias\", \n  \"public\".\"processos\".\"id_tribunal\"\n FROM \"public\".\"processos\"")
										.toString()));
								component_parameters.put("SPECIFY_DATASOURCE_ALIAS", String.valueOf("false"));
								component_parameters.put("PROPERTIES", String.valueOf("processos_judiciais"));
								component_parameters.put("USE_CURSOR", String.valueOf("false"));
								component_parameters.put("TRIM_ALL_COLUMN", String.valueOf("false"));
								component_parameters.put("TRIM_COLUMN",
										String.valueOf(new StringBuilder().append("[{TRIM=").append("false")
												.append(", SCHEMA_COLUMN=").append("id_processo").append("}, {TRIM=")
												.append("false").append(", SCHEMA_COLUMN=").append("num_processo")
												.append("}, {TRIM=").append("false").append(", SCHEMA_COLUMN=")
												.append("data_abertura").append("}, {TRIM=").append("false")
												.append(", SCHEMA_COLUMN=").append("classe").append("}, {TRIM=")
												.append("false").append(", SCHEMA_COLUMN=").append("tribunal")
												.append("}]").toString()));
								component_parameters.put("UNIFIED_COMPONENTS", String.valueOf("tPostgresqlInput"));

							} catch (java.lang.Exception e_Processos_Ingestao_Bronze_1_tDBInput_1) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("Processos_Ingestao_Bronze_1_tDBInput_1",
							"tPostgresqlInput",
							new ParameterUtil_Processos_Ingestao_Bronze_1_tDBInput_1().getParameter());
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("Processos_Ingestao_Bronze_1_tDBInput_1", "tDBInput_1", "tPostgresqlInput");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				int nb_line_Processos_Ingestao_Bronze_1_tDBInput_1 = 0;
				java.sql.Connection conn_Processos_Ingestao_Bronze_1_tDBInput_1 = null;
				String driverClass_Processos_Ingestao_Bronze_1_tDBInput_1 = "org.postgresql.Driver";
				java.lang.Class jdbcclazz_Processos_Ingestao_Bronze_1_tDBInput_1 = java.lang.Class
						.forName(driverClass_Processos_Ingestao_Bronze_1_tDBInput_1);
				String dbUser_Processos_Ingestao_Bronze_1_tDBInput_1 = "peta_qlik";

				final String decryptedPassword_Processos_Ingestao_Bronze_1_tDBInput_1 = java.util.Optional
						.ofNullable(routines.system.PasswordEncryptUtil.decryptPassword(
								"enc:routine.encryption.key.v1:km7/MGCqAHpaqjlOxG/RjAMebBimeYymiIeS9aD1rbpUYcf6QQ=="))
						.orElse("");

				String dbPwd_Processos_Ingestao_Bronze_1_tDBInput_1 = decryptedPassword_Processos_Ingestao_Bronze_1_tDBInput_1;

				String url_Processos_Ingestao_Bronze_1_tDBInput_1 = "jdbc:postgresql://"
						+ "peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com" + ":" + "5432" + "/"
						+ "qlik_demo" + "?" + "processos_judiciais";

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_1 - Driver ClassName: "
						+ driverClass_Processos_Ingestao_Bronze_1_tDBInput_1 + ".");

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_1 - Connection attempt to '"
						+ url_Processos_Ingestao_Bronze_1_tDBInput_1.replaceAll("(?<=trustStorePassword=)[^;]*",
								"********")
						+ "' with the username '" + dbUser_Processos_Ingestao_Bronze_1_tDBInput_1 + "'.");

				conn_Processos_Ingestao_Bronze_1_tDBInput_1 = java.sql.DriverManager.getConnection(
						url_Processos_Ingestao_Bronze_1_tDBInput_1, dbUser_Processos_Ingestao_Bronze_1_tDBInput_1,
						dbPwd_Processos_Ingestao_Bronze_1_tDBInput_1);
				log.debug("Processos_Ingestao_Bronze_1_tDBInput_1 - Connection to '"
						+ url_Processos_Ingestao_Bronze_1_tDBInput_1.replaceAll("(?<=trustStorePassword=)[^;]*",
								"********")
						+ "' has succeeded.");

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_1 - Connection is set auto commit to 'false'.");

				conn_Processos_Ingestao_Bronze_1_tDBInput_1.setAutoCommit(false);

				java.sql.Statement stmt_Processos_Ingestao_Bronze_1_tDBInput_1 = conn_Processos_Ingestao_Bronze_1_tDBInput_1
						.createStatement();

				String dbquery_Processos_Ingestao_Bronze_1_tDBInput_1 = new StringBuilder().append(
						"SELECT \n  \"public\".\"processos\".\"id_processo\", \n  \"public\".\"processos\".\"numero_processo\", \n  \"public\".\"pr"
								+ "ocessos\".\"data_ajuizamento\", \n  \"public\".\"processos\".\"id_classe\", \n  \"public\".\"processos\".\"id_assunto\", "
								+ "\n  \"public\".\"processos\".\"id_magistrado\", \n  \"public\".\"processos\".\"valor_causa\", \n \"public\".\"processos"
								+ "\".\"tempo_processo_dias\", \n  \"public\".\"processos\".\"id_tribunal\"\n FROM \"public\".\"processos\"")
						.toString();

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_1 - Executing the query: '"
						+ dbquery_Processos_Ingestao_Bronze_1_tDBInput_1 + "'.");

				globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_1_QUERY",
						dbquery_Processos_Ingestao_Bronze_1_tDBInput_1);

				java.sql.ResultSet rs_Processos_Ingestao_Bronze_1_tDBInput_1 = null;

				try {
					rs_Processos_Ingestao_Bronze_1_tDBInput_1 = stmt_Processos_Ingestao_Bronze_1_tDBInput_1
							.executeQuery(dbquery_Processos_Ingestao_Bronze_1_tDBInput_1);
					java.sql.ResultSetMetaData rsmd_Processos_Ingestao_Bronze_1_tDBInput_1 = rs_Processos_Ingestao_Bronze_1_tDBInput_1
							.getMetaData();
					int colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_1 = rsmd_Processos_Ingestao_Bronze_1_tDBInput_1
							.getColumnCount();

					String tmpContent_Processos_Ingestao_Bronze_1_tDBInput_1 = null;

					log.debug("Processos_Ingestao_Bronze_1_tDBInput_1 - Retrieving records from the database.");

					while (rs_Processos_Ingestao_Bronze_1_tDBInput_1.next()) {
						nb_line_Processos_Ingestao_Bronze_1_tDBInput_1++;

						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_1 < 1) {
							Processos_Ingestao_Bronze_1_row1.id_processo = 0;
						} else {

							Processos_Ingestao_Bronze_1_row1.id_processo = rs_Processos_Ingestao_Bronze_1_tDBInput_1
									.getInt(1);
							if (rs_Processos_Ingestao_Bronze_1_tDBInput_1.wasNull()) {
								throw new RuntimeException("Null value in non-Nullable column");
							}
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_1 < 2) {
							Processos_Ingestao_Bronze_1_row1.num_processo = null;
						} else {

							Processos_Ingestao_Bronze_1_row1.num_processo = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_1, 2, false);
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_1 < 3) {
							Processos_Ingestao_Bronze_1_row1.data_abertura = null;
						} else {

							Processos_Ingestao_Bronze_1_row1.data_abertura = routines.system.JDBCUtil
									.getDate(rs_Processos_Ingestao_Bronze_1_tDBInput_1, 3);
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_1 < 4) {
							Processos_Ingestao_Bronze_1_row1.classe = null;
						} else {

							Processos_Ingestao_Bronze_1_row1.classe = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_1, 4, false);
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_1 < 5) {
							Processos_Ingestao_Bronze_1_row1.tribunal = null;
						} else {

							Processos_Ingestao_Bronze_1_row1.tribunal = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_1, 5, false);
						}

						log.debug("Processos_Ingestao_Bronze_1_tDBInput_1 - Retrieving the record "
								+ nb_line_Processos_Ingestao_Bronze_1_tDBInput_1 + ".");

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_1 begin ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_1 main ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_1");

						// QTUP-3575
						if (enableLineage && init_Processos_Ingestao_Bronze_1_tDBInput_1_0) {
							class SchemaUtil_Processos_Ingestao_Bronze_1_row1 {

								private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
									java.util.Map<String, String> field = new java.util.HashMap<>();
									field.put("name", values[0]);
									field.put("origin_name", values[1]);
									field.put("iskey", values[2]);
									field.put("talend_type", values[3]);
									field.put("type", values[4]);
									field.put("nullable", values[5]);
									field.put("pattern", values[6]);
									field.put("length", values[7]);
									field.put("precision", values[8]);
									schema.add(field);
								}

								public java.util.List<java.util.Map<String, String>> getSchema(
										final Processos_Ingestao_Bronze_1_row1Struct Processos_Ingestao_Bronze_1_row1) {
									java.util.List<java.util.Map<String, String>> s = new java.util.ArrayList<>();
									if (Processos_Ingestao_Bronze_1_row1 == null) {
										return s;
									}
									a(s, "id_processo", "id_processo", "true", "id_Integer", "SERIAL", "false", "",
											"10", "0");
									a(s, "num_processo", "num_processo", "false", "id_String", "VARCHAR", "false", "",
											"25", "0");
									a(s, "data_abertura", "data_abertura", "false", "id_Date", "DATE", "false",
											"dd-MM-yyyy", "13", "0");
									a(s, "classe", "classe", "false", "id_String", "VARCHAR", "true", "", "100", "0");
									a(s, "tribunal", "tribunal", "false", "id_String", "VARCHAR", "true", "", "10",
											"0");
									return s;
								}

							}

							if (Processos_Ingestao_Bronze_1_row1 != null) {
								talendJobLog.addConnectionSchemaMessage("Processos_Ingestao_Bronze_1_tDBInput_1",
										"tPostgresqlInput", "Processos_Ingestao_Bronze_1_tFileOutputParquet_1",
										"tFileOutputParquet", "Processos_Ingestao_Bronze_1_row1" + iterateId,
										new SchemaUtil_Processos_Ingestao_Bronze_1_row1()
												.getSchema(Processos_Ingestao_Bronze_1_row1));
								talendJobLogProcess(globalMap);
								init_Processos_Ingestao_Bronze_1_tDBInput_1_0 = false;
							}

						}
						// QTUP-3575

						tos_count_Processos_Ingestao_Bronze_1_tDBInput_1++;

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_1 main ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_1 process_data_begin ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_1");

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_1 process_data_begin ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_1 main ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_1");

						if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

								, "Processos_Ingestao_Bronze_1_row1", "Processos_Ingestao_Bronze_1_tDBInput_1",
								"tDBInput_1", "tPostgresqlInput", "Processos_Ingestao_Bronze_1_tFileOutputParquet_1",
								"tFileOutputParquet_1", "tFileOutputParquet"

						)) {
							talendJobLogProcess(globalMap);
						}

						if (log.isTraceEnabled()) {
							log.trace("Processos_Ingestao_Bronze_1_row1 - "
									+ (Processos_Ingestao_Bronze_1_row1 == null ? ""
											: Processos_Ingestao_Bronze_1_row1.toLogString()));
						}

						org.talend.parquet.data.Group group_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = factory_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
								.newGroup();

						group_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.append("id_processo",
								Processos_Ingestao_Bronze_1_row1.id_processo);
						if (Processos_Ingestao_Bronze_1_row1.num_processo != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.append("num_processo",
									String.valueOf(Processos_Ingestao_Bronze_1_row1.num_processo));
						}

						if (Processos_Ingestao_Bronze_1_row1.data_abertura != null) {

							if (messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.getType("data_abertura")
									.isPrimitive()
									&& org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.INT64 == messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
											.getType("data_abertura").asPrimitiveType().getPrimitiveTypeName()) {
								group_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.append("data_abertura",
										Processos_Ingestao_Bronze_1_row1.data_abertura.getTime());
							} else {
								group_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.append("data_abertura",
										FormatterUtils.format_Date(Processos_Ingestao_Bronze_1_row1.data_abertura,
												"dd-MM-yyyy"));
							}
						}

						if (Processos_Ingestao_Bronze_1_row1.classe != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.append("classe",
									String.valueOf(Processos_Ingestao_Bronze_1_row1.classe));
						}

						if (Processos_Ingestao_Bronze_1_row1.tribunal != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.append("tribunal",
									String.valueOf(Processos_Ingestao_Bronze_1_row1.tribunal));
						}

						writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
								.write(group_Processos_Ingestao_Bronze_1_tFileOutputParquet_1);
						nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_1++;
						log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_1 - Writing the record "
								+ nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 + " to the file.");

						tos_count_Processos_Ingestao_Bronze_1_tFileOutputParquet_1++;

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_1 main ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_1 process_data_begin ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_1");

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_1 process_data_begin ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_1 process_data_end ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_1");

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_1 process_data_end ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_1 process_data_end ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_1");

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_1 process_data_end ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_1 end ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_1");

					}
				} finally {
					if (rs_Processos_Ingestao_Bronze_1_tDBInput_1 != null) {
						rs_Processos_Ingestao_Bronze_1_tDBInput_1.close();
					}
					if (stmt_Processos_Ingestao_Bronze_1_tDBInput_1 != null) {
						stmt_Processos_Ingestao_Bronze_1_tDBInput_1.close();
					}
					if (conn_Processos_Ingestao_Bronze_1_tDBInput_1 != null
							&& !conn_Processos_Ingestao_Bronze_1_tDBInput_1.isClosed()) {

						log.debug("Processos_Ingestao_Bronze_1_tDBInput_1 - Closing the connection to the database.");

						conn_Processos_Ingestao_Bronze_1_tDBInput_1.close();

						if ("com.mysql.cj.jdbc.Driver".equals((String) globalMap.get("driverClass_"))
								&& routines.system.BundleUtils.inOSGi()) {
							Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread")
									.getMethod("checkedShutdown").invoke(null, (Object[]) null);
						}

						log.debug("Processos_Ingestao_Bronze_1_tDBInput_1 - Connection to the database closed.");

					}

				}
				globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_1_NB_LINE",
						nb_line_Processos_Ingestao_Bronze_1_tDBInput_1);
				log.debug("Processos_Ingestao_Bronze_1_tDBInput_1 - Retrieved records count: "
						+ nb_line_Processos_Ingestao_Bronze_1_tDBInput_1 + " .");

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tDBInput_1 - " + ("Done."));

				ok_Hash.put("Processos_Ingestao_Bronze_1_tDBInput_1", true);
				end_Hash.put("Processos_Ingestao_Bronze_1_tDBInput_1", System.currentTimeMillis());

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_1 end ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_1 end ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_1");

				globalMap.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_1_NB_LINE",
						nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_1);

				log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_1 - Written records count: "
						+ nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 + " .");

				if (writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 != null) {
					writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_1.close();
				}
				org.apache.hadoop.fs.FileSystem fs_Processos_Ingestao_Bronze_1_tFileOutputParquet_1 = crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
						.getFileSystem(config_Processos_Ingestao_Bronze_1_tFileOutputParquet_1);
				if (fs_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
						.exists(crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_1)) {
					fs_Processos_Ingestao_Bronze_1_tFileOutputParquet_1
							.delete(crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_1, false);
				}

				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId,
						"Processos_Ingestao_Bronze_1_row1", 2, 0, "Processos_Ingestao_Bronze_1_tDBInput_1",
						"tDBInput_1", "tPostgresqlInput", "Processos_Ingestao_Bronze_1_tFileOutputParquet_1",
						"tFileOutputParquet_1", "tFileOutputParquet", "output")) {
					talendJobLogProcess(globalMap);
				}

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_1 - " + ("Done."));

				ok_Hash.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_1", true);
				end_Hash.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_1", System.currentTimeMillis());

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_1 end ] stop
				 */

			} // end the resume

			if (resumeEntryMethodName == null || globalResumeTicket) {
				resumeUtil.addLog("CHECKPOINT",
						"CONNECTION:SUBJOB_OK:Processos_Ingestao_Bronze_1_tDBInput_1:OnSubjobOk1", "",
						Thread.currentThread().getId() + "", "", "", "", "", "");
			}

			if (execStat) {
				runStat.updateStatOnConnection("Processos_Ingestao_Bronze_1_OnSubjobOk1", 0, "ok");
			}

			Processos_Ingestao_Bronze_1_tDBInput_2Process(globalMap);

			if (resumeEntryMethodName == null || globalResumeTicket) {
				resumeUtil.addLog("CHECKPOINT",
						"CONNECTION:SUBJOB_OK:Processos_Ingestao_Bronze_1_tDBInput_1:OnSubjobOk2", "",
						Thread.currentThread().getId() + "", "", "", "", "", "");
			}

			if (execStat) {
				runStat.updateStatOnConnection("Processos_Ingestao_Bronze_1_OnSubjobOk3", 0, "ok");
			}

			Processos_Ingestao_Bronze_1_tDBInput_3Process(globalMap);

			if (resumeEntryMethodName == null || globalResumeTicket) {
				resumeUtil.addLog("CHECKPOINT",
						"CONNECTION:SUBJOB_OK:Processos_Ingestao_Bronze_1_tDBInput_1:OnSubjobOk3", "",
						Thread.currentThread().getId() + "", "", "", "", "", "");
			}

			if (execStat) {
				runStat.updateStatOnConnection("Processos_Ingestao_Bronze_1_OnSubjobOk4", 0, "ok");
			}

			Processos_Ingestao_Bronze_1_tDBInput_4Process(globalMap);

			if (resumeEntryMethodName == null || globalResumeTicket) {
				resumeUtil.addLog("CHECKPOINT",
						"CONNECTION:SUBJOB_OK:Processos_Ingestao_Bronze_1_tDBInput_1:OnSubjobOk4", "",
						Thread.currentThread().getId() + "", "", "", "", "", "");
			}

			if (execStat) {
				runStat.updateStatOnConnection("Processos_Ingestao_Bronze_1_OnSubjobOk5", 0, "ok");
			}

			Processos_Ingestao_Bronze_1_tDBInput_5Process(globalMap);

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException) && !(e instanceof TDieException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_1 finally ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_1");

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_1 finally ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_1 finally ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_1");

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_1_SUBPROCESS_STATE", 1);
	}

	public static class Processos_Ingestao_Bronze_1_row2Struct
			implements routines.system.IPersistableRow<Processos_Ingestao_Bronze_1_row2Struct> {
		final static byte[] commonByteArrayLock_DEMOTRF5_TRF5 = new byte[0];
		static byte[] commonByteArray_DEMOTRF5_TRF5 = new byte[0];

		public int id_tribunal;

		public int getId_tribunal() {
			return this.id_tribunal;
		}

		public Boolean id_tribunalIsNullable() {
			return false;
		}

		public Boolean id_tribunalIsKey() {
			return false;
		}

		public Integer id_tribunalLength() {
			return 10;
		}

		public Integer id_tribunalPrecision() {
			return 0;
		}

		public String id_tribunalDefault() {

			return "";

		}

		public String id_tribunalComment() {

			return "";

		}

		public String id_tribunalPattern() {

			return "";

		}

		public String id_tribunalOriginalDbColumnName() {

			return "id_tribunal";

		}

		public String nome_tribunal;

		public String getNome_tribunal() {
			return this.nome_tribunal;
		}

		public Boolean nome_tribunalIsNullable() {
			return true;
		}

		public Boolean nome_tribunalIsKey() {
			return false;
		}

		public Integer nome_tribunalLength() {
			return 255;
		}

		public Integer nome_tribunalPrecision() {
			return 0;
		}

		public String nome_tribunalDefault() {

			return null;

		}

		public String nome_tribunalComment() {

			return "";

		}

		public String nome_tribunalPattern() {

			return "";

		}

		public String nome_tribunalOriginalDbColumnName() {

			return "nome_tribunal";

		}

		public String sigla;

		public String getSigla() {
			return this.sigla;
		}

		public Boolean siglaIsNullable() {
			return true;
		}

		public Boolean siglaIsKey() {
			return false;
		}

		public Integer siglaLength() {
			return 20;
		}

		public Integer siglaPrecision() {
			return 0;
		}

		public String siglaDefault() {

			return null;

		}

		public String siglaComment() {

			return "";

		}

		public String siglaPattern() {

			return "";

		}

		public String siglaOriginalDbColumnName() {

			return "sigla";

		}

		public String regiao;

		public String getRegiao() {
			return this.regiao;
		}

		public Boolean regiaoIsNullable() {
			return true;
		}

		public Boolean regiaoIsKey() {
			return false;
		}

		public Integer regiaoLength() {
			return 50;
		}

		public Integer regiaoPrecision() {
			return 0;
		}

		public String regiaoDefault() {

			return null;

		}

		public String regiaoComment() {

			return "";

		}

		public String regiaoPattern() {

			return "";

		}

		public String regiaoOriginalDbColumnName() {

			return "regiao";

		}

		public String porte_tribunal;

		public String getPorte_tribunal() {
			return this.porte_tribunal;
		}

		public Boolean porte_tribunalIsNullable() {
			return true;
		}

		public Boolean porte_tribunalIsKey() {
			return false;
		}

		public Integer porte_tribunalLength() {
			return 50;
		}

		public Integer porte_tribunalPrecision() {
			return 0;
		}

		public String porte_tribunalDefault() {

			return null;

		}

		public String porte_tribunalComment() {

			return "";

		}

		public String porte_tribunalPattern() {

			return "";

		}

		public String porte_tribunalOriginalDbColumnName() {

			return "porte_tribunal";

		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_DEMOTRF5_TRF5.length) {
					if (length < 1024 && commonByteArray_DEMOTRF5_TRF5.length == 0) {
						commonByteArray_DEMOTRF5_TRF5 = new byte[1024];
					} else {
						commonByteArray_DEMOTRF5_TRF5 = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_DEMOTRF5_TRF5, 0, length);
				strReturn = new String(commonByteArray_DEMOTRF5_TRF5, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			String strReturn = null;
			int length = 0;
			length = unmarshaller.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_DEMOTRF5_TRF5.length) {
					if (length < 1024 && commonByteArray_DEMOTRF5_TRF5.length == 0) {
						commonByteArray_DEMOTRF5_TRF5 = new byte[1024];
					} else {
						commonByteArray_DEMOTRF5_TRF5 = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_DEMOTRF5_TRF5, 0, length);
				strReturn = new String(commonByteArray_DEMOTRF5_TRF5, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos) throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (str == null) {
				marshaller.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				marshaller.writeInt(byteArray.length);
				marshaller.write(byteArray);
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_DEMOTRF5_TRF5) {

				try {

					int length = 0;

					this.id_tribunal = dis.readInt();

					this.nome_tribunal = readString(dis);

					this.sigla = readString(dis);

					this.regiao = readString(dis);

					this.porte_tribunal = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_DEMOTRF5_TRF5) {

				try {

					int length = 0;

					this.id_tribunal = dis.readInt();

					this.nome_tribunal = readString(dis);

					this.sigla = readString(dis);

					this.regiao = readString(dis);

					this.porte_tribunal = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// int

				dos.writeInt(this.id_tribunal);

				// String

				writeString(this.nome_tribunal, dos);

				// String

				writeString(this.sigla, dos);

				// String

				writeString(this.regiao, dos);

				// String

				writeString(this.porte_tribunal, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// int

				dos.writeInt(this.id_tribunal);

				// String

				writeString(this.nome_tribunal, dos);

				// String

				writeString(this.sigla, dos);

				// String

				writeString(this.regiao, dos);

				// String

				writeString(this.porte_tribunal, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("id_tribunal=" + String.valueOf(id_tribunal));
			sb.append(",nome_tribunal=" + nome_tribunal);
			sb.append(",sigla=" + sigla);
			sb.append(",regiao=" + regiao);
			sb.append(",porte_tribunal=" + porte_tribunal);
			sb.append("]");

			return sb.toString();
		}

		public String toLogString() {
			StringBuilder sb = new StringBuilder();

			sb.append(id_tribunal);

			sb.append("|");

			if (nome_tribunal == null) {
				sb.append("<null>");
			} else {
				sb.append(nome_tribunal);
			}

			sb.append("|");

			if (sigla == null) {
				sb.append("<null>");
			} else {
				sb.append(sigla);
			}

			sb.append("|");

			if (regiao == null) {
				sb.append("<null>");
			} else {
				sb.append(regiao);
			}

			sb.append("|");

			if (porte_tribunal == null) {
				sb.append("<null>");
			} else {
				sb.append(porte_tribunal);
			}

			sb.append("|");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(Processos_Ingestao_Bronze_1_row2Struct other) {

			int returnValue = -1;

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(), object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public void Processos_Ingestao_Bronze_1_tDBInput_2Process(final java.util.Map<String, Object> globalMap)
			throws TalendException {
		globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_2_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("Processos_Ingestao_Bronze_1_tDBInput_2", "rqXTUE_");

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				Processos_Ingestao_Bronze_1_row2Struct Processos_Ingestao_Bronze_1_row2 = new Processos_Ingestao_Bronze_1_row2Struct();

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_2 begin ] start
				 */

				sh("Processos_Ingestao_Bronze_1_tFileOutputParquet_2");

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_2");

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0,
						"Processos_Ingestao_Bronze_1_row2");

				int tos_count_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = 0;

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_2 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = new StringBuilder();
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.append("Parameters:");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.append("FILENAME" + " = "
									+ "\"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/tribunal_bronze\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
									.append("FILE_ACTION" + " = " + "OVERWRITE");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
									.append("COMPRESSION" + " = " + "\"UNCOMPRESSED\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
									.append("USE_EXTERNAL_HADOOP_DEPS" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
									.append("ROW_GROUP_SIZE" + " = " + "134217728");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
									.append("PAGE_SIZE" + " = " + "1048576");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.append(" | ");
							if (log.isDebugEnabled())
								log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_2 - "
										+ (log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_2));
						}
					}
					new BytesLimit65535_Processos_Ingestao_Bronze_1_tFileOutputParquet_2().limitLog4jByte();
				}
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter(
								final Processos_Ingestao_Bronze_1_row2Struct Processos_Ingestao_Bronze_1_row2)
								throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("FILENAME", String.valueOf(
										"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/tribunal_bronze"));
								component_parameters.put("FILE_ACTION", String.valueOf("OVERWRITE"));
								component_parameters.put("COMPRESSION", String.valueOf("UNCOMPRESSED"));
								component_parameters.put("USE_EXTERNAL_HADOOP_DEPS", String.valueOf("false"));
								component_parameters.put("ROW_GROUP_SIZE", String.valueOf("134217728"));
								component_parameters.put("PAGE_SIZE", String.valueOf("1048576"));

							} catch (java.lang.Exception e_Processos_Ingestao_Bronze_1_tFileOutputParquet_2) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("Processos_Ingestao_Bronze_1_tFileOutputParquet_2",
							"tFileOutputParquet", new ParameterUtil_Processos_Ingestao_Bronze_1_tFileOutputParquet_2()
									.getParameter(Processos_Ingestao_Bronze_1_row2));
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("Processos_Ingestao_Bronze_1_tFileOutputParquet_2", "tFileOutputParquet_2",
							"tFileOutputParquet");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				int nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = 0;
				org.apache.parquet.schema.Types.MessageTypeBuilder schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = org.apache.parquet.schema.Types
						.buildMessage();
				org.apache.parquet.schema.MessageType messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = null;
				org.talend.parquet.data.simple.SimpleGroupFactory factory_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = null;
				org.apache.parquet.hadoop.ParquetWriter<org.talend.parquet.data.Group> writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = null;

				String filePath_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = "C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/tribunal_bronze";
				globalMap.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_2_FILE_PATH",
						filePath_Processos_Ingestao_Bronze_1_tFileOutputParquet_2);
				org.apache.hadoop.conf.Configuration config_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = new org.apache.hadoop.conf.Configuration();
				config_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.set("fs.file.impl",
						org.apache.hadoop.fs.LocalFileSystem.class.getName());
				org.apache.hadoop.fs.Path path_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = new org.apache.hadoop.fs.Path(
						filePath_Processos_Ingestao_Bronze_1_tFileOutputParquet_2);
				// CRC file path
				String crcName_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = "."
						+ path_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.getName() + ".crc";
				org.apache.hadoop.fs.Path crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = new org.apache.hadoop.fs.Path(
						path_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.getParent(),
						crcName_Processos_Ingestao_Bronze_1_tFileOutputParquet_2);
				String compressName_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = "UNCOMPRESSED";
				int rowGroupSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = 134217728;
				int pageSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = 1048576;
				java.util.Map<String, org.talend.parquet.data.simple.SimpleGroupFactory> cachedFactory_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = new java.util.HashMap<>();
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("id_tribunal", false,
								"INT32", "INT_32"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("nome_tribunal", true,
								"BINARY", "UTF8"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("sigla", true,
								"BINARY", "UTF8"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("regiao", true,
								"BINARY", "UTF8"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("porte_tribunal",
								true, "BINARY", "UTF8"));
				messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
						.named("Schema");

				org.talend.parquet.hadoop.TalendGroupWriteSupport.setSchema(
						messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_2,
						config_Processos_Ingestao_Bronze_1_tFileOutputParquet_2);
				factory_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = new org.talend.parquet.data.simple.SimpleGroupFactory(
						messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_2);
				org.apache.parquet.hadoop.ParquetWriter.Builder<org.talend.parquet.data.Group, org.talend.parquet.hadoop.TalendParquetWriter.Builder> builder_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = org.talend.parquet.hadoop.TalendParquetWriter
						.builder(org.apache.parquet.hadoop.util.HadoopOutputFile.fromPath(
								path_Processos_Ingestao_Bronze_1_tFileOutputParquet_2,
								config_Processos_Ingestao_Bronze_1_tFileOutputParquet_2));
				builder_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
						.withWriteMode(org.apache.parquet.hadoop.ParquetFileWriter.Mode.OVERWRITE)
						.withCompressionCodec(org.apache.parquet.hadoop.metadata.CompressionCodecName
								.fromConf(compressName_Processos_Ingestao_Bronze_1_tFileOutputParquet_2))
						.withRowGroupSize(rowGroupSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_2)
						.withPageSize(pageSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_2)
						.withConf(config_Processos_Ingestao_Bronze_1_tFileOutputParquet_2);

				writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = builder_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
						.build();

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_2 begin ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_2 begin ] start
				 */

				sh("Processos_Ingestao_Bronze_1_tDBInput_2");

				s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_2");

				int tos_count_Processos_Ingestao_Bronze_1_tDBInput_2 = 0;

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tDBInput_2 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_Processos_Ingestao_Bronze_1_tDBInput_2 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2 = new StringBuilder();
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append("Parameters:");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2
									.append("USE_EXISTING_CONNECTION" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append("DB_VERSION" + " = " + "V9_X");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append("HOST" + " = "
									+ "\"peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append("PORT" + " = " + "\"5432\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2
									.append("DBNAME" + " = " + "\"qlik_demo\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2
									.append("SCHEMA_DB" + " = " + "\"public\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2
									.append("USER" + " = " + "\"peta_qlik\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append("PASS" + " = "
									+ String.valueOf(
											"enc:routine.encryption.key.v1:8PhX+A16sI9TLV6hXw4ZBcyjVjD9Nbnj4U3CcJHD1NWRWlDl0w==")
											.substring(0, 4)
									+ "...");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append("QUERYSTORE" + " = " + "\"\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append("QUERY" + " = "
									+ "\"SELECT    \\\"public\\\".\\\"tribunal\\\".\\\"id_tribunal\\\",    \\\"public\\\".\\\"tribunal\\\".\\\"nome_tribunal\\\",    \\\"public\\\".\\\"tribunal\\\".\\\"sigla\\\",    \\\"public\\\".\\\"tribunal\\\".\\\"regiao\\\",    \\\"public\\\".\\\"tribunal\\\".\\\"porte_tribunal\\\"  FROM \\\"public\\\".\\\"tribunal\\\"\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2
									.append("SPECIFY_DATASOURCE_ALIAS" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2
									.append("PROPERTIES" + " = " + "\"processos_judiciais\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2
									.append("USE_CURSOR" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2
									.append("TRIM_ALL_COLUMN" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2
									.append("TRIM_COLUMN" + " = " + "[{TRIM=" + ("false") + ", SCHEMA_COLUMN="
											+ ("id_tribunal") + "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN="
											+ ("nome_tribunal") + "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN="
											+ ("sigla") + "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("regiao")
											+ "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("porte_tribunal") + "}]");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2
									.append("UNIFIED_COMPONENTS" + " = " + "tPostgresqlInput");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2.append(" | ");
							if (log.isDebugEnabled())
								log.debug("Processos_Ingestao_Bronze_1_tDBInput_2 - "
										+ (log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_2));
						}
					}
					new BytesLimit65535_Processos_Ingestao_Bronze_1_tDBInput_2().limitLog4jByte();
				}
				boolean init_Processos_Ingestao_Bronze_1_tDBInput_2_0 = true;
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_Processos_Ingestao_Bronze_1_tDBInput_2 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter() throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("USE_EXISTING_CONNECTION", String.valueOf("false"));
								component_parameters.put("DB_VERSION", String.valueOf("V9_X"));
								component_parameters.put("HOST", String
										.valueOf("peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com"));
								component_parameters.put("PORT", String.valueOf("5432"));
								component_parameters.put("DBNAME", String.valueOf("qlik_demo"));
								component_parameters.put("SCHEMA_DB", String.valueOf("public"));
								component_parameters.put("USER", String.valueOf("peta_qlik"));
								component_parameters.put("QUERYSTORE", String.valueOf(""));
								component_parameters.put("QUERY", String.valueOf(new StringBuilder().append(
										"SELECT \n  \"public\".\"tribunal\".\"id_tribunal\", \n  \"public\".\"tribunal\".\"nome_tribunal\", \n  \"public\".\"tribun"
												+ "al\".\"sigla\", \n  \"public\".\"tribunal\".\"regiao\", \n  \"public\".\"tribunal\".\"porte_tribunal\"\n FROM \"public\".\""
												+ "tribunal\"")
										.toString()));
								component_parameters.put("SPECIFY_DATASOURCE_ALIAS", String.valueOf("false"));
								component_parameters.put("PROPERTIES", String.valueOf("processos_judiciais"));
								component_parameters.put("USE_CURSOR", String.valueOf("false"));
								component_parameters.put("TRIM_ALL_COLUMN", String.valueOf("false"));
								component_parameters.put("TRIM_COLUMN",
										String.valueOf(new StringBuilder().append("[{TRIM=").append("false")
												.append(", SCHEMA_COLUMN=").append("id_tribunal").append("}, {TRIM=")
												.append("false").append(", SCHEMA_COLUMN=").append("nome_tribunal")
												.append("}, {TRIM=").append("false").append(", SCHEMA_COLUMN=")
												.append("sigla").append("}, {TRIM=").append("false")
												.append(", SCHEMA_COLUMN=").append("regiao").append("}, {TRIM=")
												.append("false").append(", SCHEMA_COLUMN=").append("porte_tribunal")
												.append("}]").toString()));
								component_parameters.put("UNIFIED_COMPONENTS", String.valueOf("tPostgresqlInput"));

							} catch (java.lang.Exception e_Processos_Ingestao_Bronze_1_tDBInput_2) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("Processos_Ingestao_Bronze_1_tDBInput_2",
							"tPostgresqlInput",
							new ParameterUtil_Processos_Ingestao_Bronze_1_tDBInput_2().getParameter());
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("Processos_Ingestao_Bronze_1_tDBInput_2", "tDBInput_2", "tPostgresqlInput");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				int nb_line_Processos_Ingestao_Bronze_1_tDBInput_2 = 0;
				java.sql.Connection conn_Processos_Ingestao_Bronze_1_tDBInput_2 = null;
				String driverClass_Processos_Ingestao_Bronze_1_tDBInput_2 = "org.postgresql.Driver";
				java.lang.Class jdbcclazz_Processos_Ingestao_Bronze_1_tDBInput_2 = java.lang.Class
						.forName(driverClass_Processos_Ingestao_Bronze_1_tDBInput_2);
				String dbUser_Processos_Ingestao_Bronze_1_tDBInput_2 = "peta_qlik";

				final String decryptedPassword_Processos_Ingestao_Bronze_1_tDBInput_2 = java.util.Optional
						.ofNullable(routines.system.PasswordEncryptUtil.decryptPassword(
								"enc:routine.encryption.key.v1:XbJaLDZKAY0hF+IaNnXUr/elg+rYc4eWXYOB0BBML959wsRvmw=="))
						.orElse("");

				String dbPwd_Processos_Ingestao_Bronze_1_tDBInput_2 = decryptedPassword_Processos_Ingestao_Bronze_1_tDBInput_2;

				String url_Processos_Ingestao_Bronze_1_tDBInput_2 = "jdbc:postgresql://"
						+ "peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com" + ":" + "5432" + "/"
						+ "qlik_demo" + "?" + "processos_judiciais";

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_2 - Driver ClassName: "
						+ driverClass_Processos_Ingestao_Bronze_1_tDBInput_2 + ".");

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_2 - Connection attempt to '"
						+ url_Processos_Ingestao_Bronze_1_tDBInput_2.replaceAll("(?<=trustStorePassword=)[^;]*",
								"********")
						+ "' with the username '" + dbUser_Processos_Ingestao_Bronze_1_tDBInput_2 + "'.");

				conn_Processos_Ingestao_Bronze_1_tDBInput_2 = java.sql.DriverManager.getConnection(
						url_Processos_Ingestao_Bronze_1_tDBInput_2, dbUser_Processos_Ingestao_Bronze_1_tDBInput_2,
						dbPwd_Processos_Ingestao_Bronze_1_tDBInput_2);
				log.debug("Processos_Ingestao_Bronze_1_tDBInput_2 - Connection to '"
						+ url_Processos_Ingestao_Bronze_1_tDBInput_2.replaceAll("(?<=trustStorePassword=)[^;]*",
								"********")
						+ "' has succeeded.");

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_2 - Connection is set auto commit to 'false'.");

				conn_Processos_Ingestao_Bronze_1_tDBInput_2.setAutoCommit(false);

				java.sql.Statement stmt_Processos_Ingestao_Bronze_1_tDBInput_2 = conn_Processos_Ingestao_Bronze_1_tDBInput_2
						.createStatement();

				String dbquery_Processos_Ingestao_Bronze_1_tDBInput_2 = new StringBuilder().append(
						"SELECT \n  \"public\".\"tribunal\".\"id_tribunal\", \n  \"public\".\"tribunal\".\"nome_tribunal\", \n  \"public\".\"tribun"
								+ "al\".\"sigla\", \n  \"public\".\"tribunal\".\"regiao\", \n  \"public\".\"tribunal\".\"porte_tribunal\"\n FROM \"public\".\""
								+ "tribunal\"")
						.toString();

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_2 - Executing the query: '"
						+ dbquery_Processos_Ingestao_Bronze_1_tDBInput_2 + "'.");

				globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_2_QUERY",
						dbquery_Processos_Ingestao_Bronze_1_tDBInput_2);

				java.sql.ResultSet rs_Processos_Ingestao_Bronze_1_tDBInput_2 = null;

				try {
					rs_Processos_Ingestao_Bronze_1_tDBInput_2 = stmt_Processos_Ingestao_Bronze_1_tDBInput_2
							.executeQuery(dbquery_Processos_Ingestao_Bronze_1_tDBInput_2);
					java.sql.ResultSetMetaData rsmd_Processos_Ingestao_Bronze_1_tDBInput_2 = rs_Processos_Ingestao_Bronze_1_tDBInput_2
							.getMetaData();
					int colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_2 = rsmd_Processos_Ingestao_Bronze_1_tDBInput_2
							.getColumnCount();

					String tmpContent_Processos_Ingestao_Bronze_1_tDBInput_2 = null;

					log.debug("Processos_Ingestao_Bronze_1_tDBInput_2 - Retrieving records from the database.");

					while (rs_Processos_Ingestao_Bronze_1_tDBInput_2.next()) {
						nb_line_Processos_Ingestao_Bronze_1_tDBInput_2++;

						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_2 < 1) {
							Processos_Ingestao_Bronze_1_row2.id_tribunal = 0;
						} else {

							Processos_Ingestao_Bronze_1_row2.id_tribunal = rs_Processos_Ingestao_Bronze_1_tDBInput_2
									.getInt(1);
							if (rs_Processos_Ingestao_Bronze_1_tDBInput_2.wasNull()) {
								throw new RuntimeException("Null value in non-Nullable column");
							}
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_2 < 2) {
							Processos_Ingestao_Bronze_1_row2.nome_tribunal = null;
						} else {

							Processos_Ingestao_Bronze_1_row2.nome_tribunal = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_2, 2, false);
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_2 < 3) {
							Processos_Ingestao_Bronze_1_row2.sigla = null;
						} else {

							Processos_Ingestao_Bronze_1_row2.sigla = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_2, 3, false);
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_2 < 4) {
							Processos_Ingestao_Bronze_1_row2.regiao = null;
						} else {

							Processos_Ingestao_Bronze_1_row2.regiao = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_2, 4, false);
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_2 < 5) {
							Processos_Ingestao_Bronze_1_row2.porte_tribunal = null;
						} else {

							Processos_Ingestao_Bronze_1_row2.porte_tribunal = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_2, 5, false);
						}

						log.debug("Processos_Ingestao_Bronze_1_tDBInput_2 - Retrieving the record "
								+ nb_line_Processos_Ingestao_Bronze_1_tDBInput_2 + ".");

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_2 begin ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_2 main ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_2");

						// QTUP-3575
						if (enableLineage && init_Processos_Ingestao_Bronze_1_tDBInput_2_0) {
							class SchemaUtil_Processos_Ingestao_Bronze_1_row2 {

								private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
									java.util.Map<String, String> field = new java.util.HashMap<>();
									field.put("name", values[0]);
									field.put("origin_name", values[1]);
									field.put("iskey", values[2]);
									field.put("talend_type", values[3]);
									field.put("type", values[4]);
									field.put("nullable", values[5]);
									field.put("pattern", values[6]);
									field.put("length", values[7]);
									field.put("precision", values[8]);
									schema.add(field);
								}

								public java.util.List<java.util.Map<String, String>> getSchema(
										final Processos_Ingestao_Bronze_1_row2Struct Processos_Ingestao_Bronze_1_row2) {
									java.util.List<java.util.Map<String, String>> s = new java.util.ArrayList<>();
									if (Processos_Ingestao_Bronze_1_row2 == null) {
										return s;
									}
									a(s, "id_tribunal", "id_tribunal", "false", "id_Integer", "SERIAL", "false", "",
											"10", "0");
									a(s, "nome_tribunal", "nome_tribunal", "false", "id_String", "VARCHAR", "true", "",
											"255", "0");
									a(s, "sigla", "sigla", "false", "id_String", "VARCHAR", "true", "", "20", "0");
									a(s, "regiao", "regiao", "false", "id_String", "VARCHAR", "true", "", "50", "0");
									a(s, "porte_tribunal", "porte_tribunal", "false", "id_String", "VARCHAR", "true",
											"", "50", "0");
									return s;
								}

							}

							if (Processos_Ingestao_Bronze_1_row2 != null) {
								talendJobLog.addConnectionSchemaMessage("Processos_Ingestao_Bronze_1_tDBInput_2",
										"tPostgresqlInput", "Processos_Ingestao_Bronze_1_tFileOutputParquet_2",
										"tFileOutputParquet", "Processos_Ingestao_Bronze_1_row2" + iterateId,
										new SchemaUtil_Processos_Ingestao_Bronze_1_row2()
												.getSchema(Processos_Ingestao_Bronze_1_row2));
								talendJobLogProcess(globalMap);
								init_Processos_Ingestao_Bronze_1_tDBInput_2_0 = false;
							}

						}
						// QTUP-3575

						tos_count_Processos_Ingestao_Bronze_1_tDBInput_2++;

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_2 main ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_2 process_data_begin ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_2");

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_2 process_data_begin ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_2 main ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_2");

						if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

								, "Processos_Ingestao_Bronze_1_row2", "Processos_Ingestao_Bronze_1_tDBInput_2",
								"tDBInput_2", "tPostgresqlInput", "Processos_Ingestao_Bronze_1_tFileOutputParquet_2",
								"tFileOutputParquet_2", "tFileOutputParquet"

						)) {
							talendJobLogProcess(globalMap);
						}

						if (log.isTraceEnabled()) {
							log.trace("Processos_Ingestao_Bronze_1_row2 - "
									+ (Processos_Ingestao_Bronze_1_row2 == null ? ""
											: Processos_Ingestao_Bronze_1_row2.toLogString()));
						}

						org.talend.parquet.data.Group group_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = factory_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
								.newGroup();

						group_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.append("id_tribunal",
								Processos_Ingestao_Bronze_1_row2.id_tribunal);
						if (Processos_Ingestao_Bronze_1_row2.nome_tribunal != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.append("nome_tribunal",
									String.valueOf(Processos_Ingestao_Bronze_1_row2.nome_tribunal));
						}

						if (Processos_Ingestao_Bronze_1_row2.sigla != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.append("sigla",
									String.valueOf(Processos_Ingestao_Bronze_1_row2.sigla));
						}

						if (Processos_Ingestao_Bronze_1_row2.regiao != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.append("regiao",
									String.valueOf(Processos_Ingestao_Bronze_1_row2.regiao));
						}

						if (Processos_Ingestao_Bronze_1_row2.porte_tribunal != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.append("porte_tribunal",
									String.valueOf(Processos_Ingestao_Bronze_1_row2.porte_tribunal));
						}

						writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
								.write(group_Processos_Ingestao_Bronze_1_tFileOutputParquet_2);
						nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_2++;
						log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_2 - Writing the record "
								+ nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 + " to the file.");

						tos_count_Processos_Ingestao_Bronze_1_tFileOutputParquet_2++;

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_2 main ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_2 process_data_begin ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_2");

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_2 process_data_begin ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_2 process_data_end ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_2");

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_2 process_data_end ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_2 process_data_end ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_2");

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_2 process_data_end ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_2 end ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_2");

					}
				} finally {
					if (rs_Processos_Ingestao_Bronze_1_tDBInput_2 != null) {
						rs_Processos_Ingestao_Bronze_1_tDBInput_2.close();
					}
					if (stmt_Processos_Ingestao_Bronze_1_tDBInput_2 != null) {
						stmt_Processos_Ingestao_Bronze_1_tDBInput_2.close();
					}
					if (conn_Processos_Ingestao_Bronze_1_tDBInput_2 != null
							&& !conn_Processos_Ingestao_Bronze_1_tDBInput_2.isClosed()) {

						log.debug("Processos_Ingestao_Bronze_1_tDBInput_2 - Closing the connection to the database.");

						conn_Processos_Ingestao_Bronze_1_tDBInput_2.close();

						if ("com.mysql.cj.jdbc.Driver".equals((String) globalMap.get("driverClass_"))
								&& routines.system.BundleUtils.inOSGi()) {
							Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread")
									.getMethod("checkedShutdown").invoke(null, (Object[]) null);
						}

						log.debug("Processos_Ingestao_Bronze_1_tDBInput_2 - Connection to the database closed.");

					}

				}
				globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_2_NB_LINE",
						nb_line_Processos_Ingestao_Bronze_1_tDBInput_2);
				log.debug("Processos_Ingestao_Bronze_1_tDBInput_2 - Retrieved records count: "
						+ nb_line_Processos_Ingestao_Bronze_1_tDBInput_2 + " .");

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tDBInput_2 - " + ("Done."));

				ok_Hash.put("Processos_Ingestao_Bronze_1_tDBInput_2", true);
				end_Hash.put("Processos_Ingestao_Bronze_1_tDBInput_2", System.currentTimeMillis());

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_2 end ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_2 end ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_2");

				globalMap.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_2_NB_LINE",
						nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_2);

				log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_2 - Written records count: "
						+ nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 + " .");

				if (writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 != null) {
					writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_2.close();
				}
				org.apache.hadoop.fs.FileSystem fs_Processos_Ingestao_Bronze_1_tFileOutputParquet_2 = crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
						.getFileSystem(config_Processos_Ingestao_Bronze_1_tFileOutputParquet_2);
				if (fs_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
						.exists(crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_2)) {
					fs_Processos_Ingestao_Bronze_1_tFileOutputParquet_2
							.delete(crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_2, false);
				}

				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId,
						"Processos_Ingestao_Bronze_1_row2", 2, 0, "Processos_Ingestao_Bronze_1_tDBInput_2",
						"tDBInput_2", "tPostgresqlInput", "Processos_Ingestao_Bronze_1_tFileOutputParquet_2",
						"tFileOutputParquet_2", "tFileOutputParquet", "output")) {
					talendJobLogProcess(globalMap);
				}

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_2 - " + ("Done."));

				ok_Hash.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_2", true);
				end_Hash.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_2", System.currentTimeMillis());

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_2 end ] stop
				 */

			} // end the resume

			if (resumeEntryMethodName == null || globalResumeTicket) {
				resumeUtil.addLog("CHECKPOINT",
						"CONNECTION:SUBJOB_OK:Processos_Ingestao_Bronze_1_tDBInput_2:OnSubjobOk", "",
						Thread.currentThread().getId() + "", "", "", "", "", "");
			}

			if (execStat) {
				runStat.updateStatOnConnection("Processos_Ingestao_Bronze_1_OnSubjobOk2", 0, "ok");
			}

			Processos_Ingestao_Bronze_1_tFileList_1Process(globalMap);

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException) && !(e instanceof TDieException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_2 finally ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_2");

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_2 finally ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_2 finally ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_2");

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_2 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_2_SUBPROCESS_STATE", 1);
	}

	public void Processos_Ingestao_Bronze_1_tFileList_1Process(final java.util.Map<String, Object> globalMap)
			throws TalendException {
		globalMap.put("Processos_Ingestao_Bronze_1_tFileList_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("Processos_Ingestao_Bronze_1_tFileList_1", "Z0g5xE_");

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				/**
				 * [Processos_Ingestao_Bronze_1_tFileList_1 begin ] start
				 */

				int NB_ITERATE_Processos_Ingestao_Bronze_1_tS3Put_1 = 0; // for statistics

				sh("Processos_Ingestao_Bronze_1_tFileList_1");

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileList_1");

				int tos_count_Processos_Ingestao_Bronze_1_tFileList_1 = 0;

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tFileList_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_Processos_Ingestao_Bronze_1_tFileList_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1 = new StringBuilder();
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append("Parameters:");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append(
									"DIRECTORY" + " = " + "\"C:/Program Files (x86)/Talend-Studio/studio/workspace\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1
									.append("LIST_MODE" + " = " + "FILES");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1
									.append("INCLUDSUBDIR" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1
									.append("CASE_SENSITIVE" + " = " + "YES");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append("ERROR" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1
									.append("GLOBEXPRESSIONS" + " = " + "true");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append("FILES" + " = " + "[]");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1
									.append("ORDER_BY_NOTHING" + " = " + "true");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1
									.append("ORDER_BY_FILENAME" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1
									.append("ORDER_BY_FILESIZE" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1
									.append("ORDER_BY_MODIFIEDDATE" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1
									.append("ORDER_ACTION_ASC" + " = " + "true");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1
									.append("ORDER_ACTION_DESC" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1
									.append("IFEXCLUDE" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1
									.append("FORMAT_FILEPATH_TO_SLASH" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("Processos_Ingestao_Bronze_1_tFileList_1 - "
										+ (log4jParamters_Processos_Ingestao_Bronze_1_tFileList_1));
						}
					}
					new BytesLimit65535_Processos_Ingestao_Bronze_1_tFileList_1().limitLog4jByte();
				}
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_Processos_Ingestao_Bronze_1_tFileList_1 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter() throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("DIRECTORY",
										String.valueOf("C:/Program Files (x86)/Talend-Studio/studio/workspace"));
								component_parameters.put("LIST_MODE", String.valueOf("FILES"));
								component_parameters.put("INCLUDSUBDIR", String.valueOf("false"));
								component_parameters.put("CASE_SENSITIVE", String.valueOf("YES"));
								component_parameters.put("ERROR", String.valueOf("false"));
								component_parameters.put("GLOBEXPRESSIONS", String.valueOf("true"));
								component_parameters.put("FILES", String.valueOf("[]"));
								component_parameters.put("ORDER_BY_NOTHING", String.valueOf("true"));
								component_parameters.put("ORDER_BY_FILENAME", String.valueOf("false"));
								component_parameters.put("ORDER_BY_FILESIZE", String.valueOf("false"));
								component_parameters.put("ORDER_BY_MODIFIEDDATE", String.valueOf("false"));
								component_parameters.put("ORDER_ACTION_ASC", String.valueOf("true"));
								component_parameters.put("ORDER_ACTION_DESC", String.valueOf("false"));
								component_parameters.put("IFEXCLUDE", String.valueOf("false"));
								component_parameters.put("FORMAT_FILEPATH_TO_SLASH", String.valueOf("false"));

							} catch (java.lang.Exception e_Processos_Ingestao_Bronze_1_tFileList_1) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("Processos_Ingestao_Bronze_1_tFileList_1", "tFileList",
							new ParameterUtil_Processos_Ingestao_Bronze_1_tFileList_1().getParameter());
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("Processos_Ingestao_Bronze_1_tFileList_1", "tFileList_1", "tFileList");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				final StringBuffer log4jSb_Processos_Ingestao_Bronze_1_tFileList_1 = new StringBuffer();

				String directory_Processos_Ingestao_Bronze_1_tFileList_1 = "C:/Program Files (x86)/Talend-Studio/studio/workspace";
				final java.util.List<String> maskList_Processos_Ingestao_Bronze_1_tFileList_1 = new java.util.ArrayList<String>();
				final java.util.List<java.util.regex.Pattern> patternList_Processos_Ingestao_Bronze_1_tFileList_1 = new java.util.ArrayList<java.util.regex.Pattern>();
				maskList_Processos_Ingestao_Bronze_1_tFileList_1.add("*");
				for (final String filemask_Processos_Ingestao_Bronze_1_tFileList_1 : maskList_Processos_Ingestao_Bronze_1_tFileList_1) {
					String filemask_compile_Processos_Ingestao_Bronze_1_tFileList_1 = filemask_Processos_Ingestao_Bronze_1_tFileList_1;

					filemask_compile_Processos_Ingestao_Bronze_1_tFileList_1 = org.talend.components.lib.StringUtils
							.globToRegex(filemask_Processos_Ingestao_Bronze_1_tFileList_1);

					java.util.regex.Pattern fileNamePattern_Processos_Ingestao_Bronze_1_tFileList_1 = java.util.regex.Pattern
							.compile(filemask_compile_Processos_Ingestao_Bronze_1_tFileList_1);
					patternList_Processos_Ingestao_Bronze_1_tFileList_1
							.add(fileNamePattern_Processos_Ingestao_Bronze_1_tFileList_1);
				}
				int NB_FILEProcessos_Ingestao_Bronze_1_tFileList_1 = 0;

				final boolean case_sensitive_Processos_Ingestao_Bronze_1_tFileList_1 = true;

				log.info("Processos_Ingestao_Bronze_1_tFileList_1 - Starting to search for matching entries.");

				final java.util.List<java.io.File> list_Processos_Ingestao_Bronze_1_tFileList_1 = new java.util.ArrayList<java.io.File>();
				final java.util.Set<String> filePath_Processos_Ingestao_Bronze_1_tFileList_1 = new java.util.HashSet<String>();
				java.io.File file_Processos_Ingestao_Bronze_1_tFileList_1 = new java.io.File(
						directory_Processos_Ingestao_Bronze_1_tFileList_1);

				file_Processos_Ingestao_Bronze_1_tFileList_1.listFiles(new java.io.FilenameFilter() {
					public boolean accept(java.io.File dir, String name) {
						java.io.File file = new java.io.File(dir, name);
						if (!file.isDirectory()) {

							String fileName_Processos_Ingestao_Bronze_1_tFileList_1 = file.getName();
							for (final java.util.regex.Pattern fileNamePattern_Processos_Ingestao_Bronze_1_tFileList_1 : patternList_Processos_Ingestao_Bronze_1_tFileList_1) {
								if (fileNamePattern_Processos_Ingestao_Bronze_1_tFileList_1
										.matcher(fileName_Processos_Ingestao_Bronze_1_tFileList_1).matches()) {
									if (!filePath_Processos_Ingestao_Bronze_1_tFileList_1
											.contains(file.getAbsolutePath())) {
										list_Processos_Ingestao_Bronze_1_tFileList_1.add(file);
										filePath_Processos_Ingestao_Bronze_1_tFileList_1.add(file.getAbsolutePath());
									}
								}
							}
						}
						return true;
					}
				});
				java.util.Collections.sort(list_Processos_Ingestao_Bronze_1_tFileList_1);

				log.info("Processos_Ingestao_Bronze_1_tFileList_1 - Start to list files.");

				for (int i_Processos_Ingestao_Bronze_1_tFileList_1 = 0; i_Processos_Ingestao_Bronze_1_tFileList_1 < list_Processos_Ingestao_Bronze_1_tFileList_1
						.size(); i_Processos_Ingestao_Bronze_1_tFileList_1++) {
					java.io.File files_Processos_Ingestao_Bronze_1_tFileList_1 = list_Processos_Ingestao_Bronze_1_tFileList_1
							.get(i_Processos_Ingestao_Bronze_1_tFileList_1);
					String fileName_Processos_Ingestao_Bronze_1_tFileList_1 = files_Processos_Ingestao_Bronze_1_tFileList_1
							.getName();

					String currentFileName_Processos_Ingestao_Bronze_1_tFileList_1 = files_Processos_Ingestao_Bronze_1_tFileList_1
							.getName();
					String currentFilePath_Processos_Ingestao_Bronze_1_tFileList_1 = files_Processos_Ingestao_Bronze_1_tFileList_1
							.getAbsolutePath();
					String currentFileDirectory_Processos_Ingestao_Bronze_1_tFileList_1 = files_Processos_Ingestao_Bronze_1_tFileList_1
							.getParent();
					String currentFileExtension_Processos_Ingestao_Bronze_1_tFileList_1 = null;

					if (files_Processos_Ingestao_Bronze_1_tFileList_1.getName().contains(".")
							&& files_Processos_Ingestao_Bronze_1_tFileList_1.isFile()) {
						currentFileExtension_Processos_Ingestao_Bronze_1_tFileList_1 = files_Processos_Ingestao_Bronze_1_tFileList_1
								.getName().substring(
										files_Processos_Ingestao_Bronze_1_tFileList_1.getName().lastIndexOf(".") + 1);
					} else {
						currentFileExtension_Processos_Ingestao_Bronze_1_tFileList_1 = "";
					}

					NB_FILEProcessos_Ingestao_Bronze_1_tFileList_1++;
					globalMap.put("Processos_Ingestao_Bronze_1_tFileList_1_CURRENT_FILE",
							currentFileName_Processos_Ingestao_Bronze_1_tFileList_1);
					globalMap.put("Processos_Ingestao_Bronze_1_tFileList_1_CURRENT_FILEPATH",
							currentFilePath_Processos_Ingestao_Bronze_1_tFileList_1);
					globalMap.put("Processos_Ingestao_Bronze_1_tFileList_1_CURRENT_FILEDIRECTORY",
							currentFileDirectory_Processos_Ingestao_Bronze_1_tFileList_1);
					globalMap.put("Processos_Ingestao_Bronze_1_tFileList_1_CURRENT_FILEEXTENSION",
							currentFileExtension_Processos_Ingestao_Bronze_1_tFileList_1);
					globalMap.put("Processos_Ingestao_Bronze_1_tFileList_1_NB_FILE",
							NB_FILEProcessos_Ingestao_Bronze_1_tFileList_1);

					log.info("Processos_Ingestao_Bronze_1_tFileList_1 - Current file or directory path : "
							+ currentFilePath_Processos_Ingestao_Bronze_1_tFileList_1);

					/**
					 * [Processos_Ingestao_Bronze_1_tFileList_1 begin ] stop
					 */

					/**
					 * [Processos_Ingestao_Bronze_1_tFileList_1 main ] start
					 */

					s(currentComponent = "Processos_Ingestao_Bronze_1_tFileList_1");

					tos_count_Processos_Ingestao_Bronze_1_tFileList_1++;

					/**
					 * [Processos_Ingestao_Bronze_1_tFileList_1 main ] stop
					 */

					/**
					 * [Processos_Ingestao_Bronze_1_tFileList_1 process_data_begin ] start
					 */

					s(currentComponent = "Processos_Ingestao_Bronze_1_tFileList_1");

					/**
					 * [Processos_Ingestao_Bronze_1_tFileList_1 process_data_begin ] stop
					 */

					NB_ITERATE_Processos_Ingestao_Bronze_1_tS3Put_1++;

					if (execStat) {
						runStat.updateStatOnConnection("Processos_Ingestao_Bronze_1_iterate1", 1,
								"exec" + NB_ITERATE_Processos_Ingestao_Bronze_1_tS3Put_1);
						// Thread.sleep(1000);
					}

					/**
					 * [Processos_Ingestao_Bronze_1_tS3Put_1 begin ] start
					 */

					sh("Processos_Ingestao_Bronze_1_tS3Put_1");

					s(currentComponent = "Processos_Ingestao_Bronze_1_tS3Put_1");

					int tos_count_Processos_Ingestao_Bronze_1_tS3Put_1 = 0;

					if (log.isDebugEnabled())
						log.debug("Processos_Ingestao_Bronze_1_tS3Put_1 - " + ("Start to work."));
					if (log.isDebugEnabled()) {
						class BytesLimit65535_Processos_Ingestao_Bronze_1_tS3Put_1 {
							public void limitLog4jByte() throws Exception {
								StringBuilder log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1 = new StringBuilder();
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1.append("Parameters:");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1
										.append("configuration.bucket" + " = " + "peta-demo-qlik");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1.append(" | ");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1.append("configuration.key" + " = "
										+ "\"bronze/\" + ((String)globalMap.get(\"Processos_Ingestao_Bronze_1_tFileList_1_CURRENT_FILE\"))");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1.append(" | ");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1.append("configuration.file" + " = "
										+ "((String)globalMap.get(\"Processos_Ingestao_Bronze_1_tFileList_1_CURRENT_FILEPATH\"))");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1.append(" | ");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1
										.append("configuration.enableServerSideEncryption" + " = " + "false");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1.append(" | ");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1
										.append("configuration.dieOnError" + " = " + "false");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1.append(" | ");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1
										.append("configuration.multipartThreshold" + " = " + "5");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1.append(" | ");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1
										.append("configuration.partSize" + " = " + "5");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1.append(" | ");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1
										.append("configuration.cannedAccessControlList" + " = " + "NONE");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1.append(" | ");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1
										.append("configuration.enableObjectLock" + " = " + "false");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1.append(" | ");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1
										.append("configuration.setObjectTags" + " = " + "false");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1.append(" | ");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1
										.append("USE_EXISTING_CONNECTION" + " = " + "true");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1.append(" | ");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1
										.append("CONNECTION" + " = " + "Processos_Ingestao_Bronze_1_tS3Connection_2");
								log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1.append(" | ");
								if (log.isDebugEnabled())
									log.debug("Processos_Ingestao_Bronze_1_tS3Put_1 - "
											+ (log4jParamters_Processos_Ingestao_Bronze_1_tS3Put_1));
							}
						}
						new BytesLimit65535_Processos_Ingestao_Bronze_1_tS3Put_1().limitLog4jByte();
					}
					// QTUP-3575
					if (enableLineage) {
						class ParameterUtil_Processos_Ingestao_Bronze_1_tS3Put_1 {

							private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
								java.util.Map<String, String> field = new java.util.HashMap<>();
								field.put("name", values[0]);
								field.put("talend_type", values[1]);
								schema.add(field);
							}

							public java.util.Map<String, String> getParameter() throws Exception {
								java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

								try {

									component_parameters.put("configuration.bucket", "peta-demo-qlik");

									component_parameters.put("configuration.key",
											String.valueOf("bronze/" + ((String) globalMap
													.get("Processos_Ingestao_Bronze_1_tFileList_1_CURRENT_FILE"))));

									component_parameters.put("configuration.file", String.valueOf(((String) globalMap
											.get("Processos_Ingestao_Bronze_1_tFileList_1_CURRENT_FILEPATH"))));

									component_parameters.put("configuration.enableServerSideEncryption", "false");

									component_parameters.put("configuration.dieOnError", "false");

									component_parameters.put("configuration.multipartThreshold", "5");

									component_parameters.put("configuration.partSize", "5");

									component_parameters.put("configuration.cannedAccessControlList", "NONE");

									component_parameters.put("configuration.enableObjectLock", "false");

									component_parameters.put("configuration.setObjectTags", "false");
									component_parameters.put("USE_EXISTING_CONNECTION", String.valueOf("true"));
									component_parameters.put("CONNECTION",
											String.valueOf("Processos_Ingestao_Bronze_1_tS3Connection_2"));

								} catch (java.lang.Exception e_Processos_Ingestao_Bronze_1_tS3Put_1) {
									// do nothing
								}

								return component_parameters;
							}
						}

						talendJobLog.addComponentParameterMessage("Processos_Ingestao_Bronze_1_tS3Put_1", "S3Put",
								new ParameterUtil_Processos_Ingestao_Bronze_1_tS3Put_1().getParameter());
						talendJobLogProcess(globalMap);
						s(currentComponent);
					}
					// QTUP-3575

					if (enableLogStash) {
						talendJobLog.addCM("Processos_Ingestao_Bronze_1_tS3Put_1", "tS3Put_1", "S3Put");
						talendJobLogProcess(globalMap);
						s(currentComponent);
					}

					final org.talend.sdk.component.runtime.manager.ComponentManager mgr_Processos_Ingestao_Bronze_1_tS3Put_1 = org.talend.sdk.component.runtime.manager.ComponentManager
							.instance();
					mgr_Processos_Ingestao_Bronze_1_tS3Put_1.autoDiscoverPluginsIfEmpty(false, true);

					final java.util.Map<String, String> configuration_Processos_Ingestao_Bronze_1_tS3Put_1 = new java.util.HashMap<>();
					final java.util.Map<String, String> registry_metadata_Processos_Ingestao_Bronze_1_tS3Put_1 = new java.util.HashMap<>();

					final class SettingHelper_Processos_Ingestao_Bronze_1_tS3Put_1 {
						final java.util.Map<String, String> configuration;

						SettingHelper_Processos_Ingestao_Bronze_1_tS3Put_1(
								final java.util.Map<String, String> configuration) {
							this.configuration = configuration;
						}

						void put(String key, String value) {
							if (value != null) {
								configuration.put(key, value);
							}
						}
					}

					final SettingHelper_Processos_Ingestao_Bronze_1_tS3Put_1 s_Processos_Ingestao_Bronze_1_tS3Put_1 = new SettingHelper_Processos_Ingestao_Bronze_1_tS3Put_1(
							configuration_Processos_Ingestao_Bronze_1_tS3Put_1);
					Object dv_Processos_Ingestao_Bronze_1_tS3Put_1;
					java.net.URL mappings_url_Processos_Ingestao_Bronze_1_tS3Put_1 = this.getClass()
							.getResource("/xmlMappings");
					globalMap.put("Processos_Ingestao_Bronze_1_tS3Put_1_MAPPINGS_URL",
							mappings_url_Processos_Ingestao_Bronze_1_tS3Put_1);
					globalMap.putIfAbsent("TALEND_PRODUCT_VERSION", "8.0");
					globalMap.putIfAbsent("TALEND_AWS_TRACKER", "APN/1.0 Talend/8.0 Studio/8.0 (Qlik Talend Cloud)");

					s_Processos_Ingestao_Bronze_1_tS3Put_1.put("configuration.bucket", "peta-demo-qlik");

					dv_Processos_Ingestao_Bronze_1_tS3Put_1 = "bronze/"
							+ ((String) globalMap.get("Processos_Ingestao_Bronze_1_tFileList_1_CURRENT_FILE"));
					if (dv_Processos_Ingestao_Bronze_1_tS3Put_1 instanceof java.io.InputStream) {
						s_Processos_Ingestao_Bronze_1_tS3Put_1.put("configuration.key",
								"\"bronze/\" + ((String)globalMap.get(\"Processos_Ingestao_Bronze_1_tFileList_1_CURRENT_FILE\"))");
					} else {
						s_Processos_Ingestao_Bronze_1_tS3Put_1.put("configuration.key", String.valueOf("bronze/"
								+ ((String) globalMap.get("Processos_Ingestao_Bronze_1_tFileList_1_CURRENT_FILE"))));
					}

					dv_Processos_Ingestao_Bronze_1_tS3Put_1 = ((String) globalMap
							.get("Processos_Ingestao_Bronze_1_tFileList_1_CURRENT_FILEPATH"));
					if (dv_Processos_Ingestao_Bronze_1_tS3Put_1 instanceof java.io.InputStream) {
						s_Processos_Ingestao_Bronze_1_tS3Put_1.put("configuration.file",
								"((String)globalMap.get(\"Processos_Ingestao_Bronze_1_tFileList_1_CURRENT_FILEPATH\"))");
					} else {
						s_Processos_Ingestao_Bronze_1_tS3Put_1.put("configuration.file", String.valueOf(
								((String) globalMap.get("Processos_Ingestao_Bronze_1_tFileList_1_CURRENT_FILEPATH"))));
					}

					s_Processos_Ingestao_Bronze_1_tS3Put_1.put("configuration.enableServerSideEncryption", "false");

					s_Processos_Ingestao_Bronze_1_tS3Put_1.put("configuration.dieOnError", "false");

					s_Processos_Ingestao_Bronze_1_tS3Put_1.put("configuration.multipartThreshold", "5");

					s_Processos_Ingestao_Bronze_1_tS3Put_1.put("configuration.partSize", "5");

					s_Processos_Ingestao_Bronze_1_tS3Put_1.put("configuration.cannedAccessControlList", "NONE");

					s_Processos_Ingestao_Bronze_1_tS3Put_1.put("configuration.enableObjectLock", "false");

					s_Processos_Ingestao_Bronze_1_tS3Put_1.put("configuration.setObjectTags", "false");

					s_Processos_Ingestao_Bronze_1_tS3Put_1.put("configuration.dataset.__version", "-1");

					s_Processos_Ingestao_Bronze_1_tS3Put_1.put("configuration.dataset.datastore.__version", "-1");
					final class SchemaSettingHelper_Processos_Ingestao_Bronze_1_tS3Put_1_1 {

						public void set(final java.util.Map<String, String> configuration) throws java.lang.Exception {
						}
					}
					new SchemaSettingHelper_Processos_Ingestao_Bronze_1_tS3Put_1_1()
							.set(configuration_Processos_Ingestao_Bronze_1_tS3Put_1);
					final class SchemaSettingHelper_Processos_Ingestao_Bronze_1_tS3Put_1_2 {

						public void set(final java.util.Map<String, String> configuration) throws java.lang.Exception {
						}
					}
					new SchemaSettingHelper_Processos_Ingestao_Bronze_1_tS3Put_1_2()
							.set(configuration_Processos_Ingestao_Bronze_1_tS3Put_1);
					final java.util.Map<String, String> config_from_connection_Processos_Ingestao_Bronze_1_tS3Put_1 = (java.util.Map<String, String>) globalMap
							.get("configuration_Processos_Ingestao_Bronze_1_tS3Connection_2");
					final String conn_param_prefix_Processos_Ingestao_Bronze_1_tS3Put_1 = "configuration.dataset.datastore";
					if (config_from_connection_Processos_Ingestao_Bronze_1_tS3Put_1 != null
							&& conn_param_prefix_Processos_Ingestao_Bronze_1_tS3Put_1 != null) {
						final String prefix_Processos_Ingestao_Bronze_1_tS3Put_1 = config_from_connection_Processos_Ingestao_Bronze_1_tS3Put_1
								.keySet().stream()
								.filter(key_Processos_Ingestao_Bronze_1_tS3Put_1 -> key_Processos_Ingestao_Bronze_1_tS3Put_1
										.endsWith(".__version"))
								.findFirst()
								.map(key_Processos_Ingestao_Bronze_1_tS3Put_1 -> key_Processos_Ingestao_Bronze_1_tS3Put_1
										.substring(0,
												key_Processos_Ingestao_Bronze_1_tS3Put_1.lastIndexOf(".__version")))
								.orElse(null);

						if (prefix_Processos_Ingestao_Bronze_1_tS3Put_1 != null) {
							config_from_connection_Processos_Ingestao_Bronze_1_tS3Put_1.entrySet().stream().filter(
									entry_Processos_Ingestao_Bronze_1_tS3Put_1 -> entry_Processos_Ingestao_Bronze_1_tS3Put_1
											.getKey().startsWith(prefix_Processos_Ingestao_Bronze_1_tS3Put_1))
									.forEach(entry_Processos_Ingestao_Bronze_1_tS3Put_1 -> {
										configuration_Processos_Ingestao_Bronze_1_tS3Put_1.put(
												entry_Processos_Ingestao_Bronze_1_tS3Put_1.getKey().replaceFirst(
														prefix_Processos_Ingestao_Bronze_1_tS3Put_1,
														conn_param_prefix_Processos_Ingestao_Bronze_1_tS3Put_1),
												entry_Processos_Ingestao_Bronze_1_tS3Put_1.getValue());
									});
						}
					}

					final org.talend.sdk.component.runtime.standalone.DriverRunner standalone_Processos_Ingestao_Bronze_1_tS3Put_1 = mgr_Processos_Ingestao_Bronze_1_tS3Put_1
							.findDriverRunner("S3", "Put", 1, configuration_Processos_Ingestao_Bronze_1_tS3Put_1)
							.orElseThrow(() -> new IllegalArgumentException("Can't find S3#Put"));

					org.talend.sdk.component.runtime.di.studio.RuntimeContextInjector.injectLifecycle(
							standalone_Processos_Ingestao_Bronze_1_tS3Put_1,
							new org.talend.sdk.component.api.context.RuntimeContextHolder(
									"Processos_Ingestao_Bronze_1_tS3Put_1", globalMap));

					try {
						java.lang.reflect.Field field_Processos_Ingestao_Bronze_1_tS3Put_1 = standalone_Processos_Ingestao_Bronze_1_tS3Put_1
								.getClass().getSuperclass().getDeclaredField("delegate");
						if (!field_Processos_Ingestao_Bronze_1_tS3Put_1.isAccessible()) {
							field_Processos_Ingestao_Bronze_1_tS3Put_1.setAccessible(true);
						}
						Object v_Processos_Ingestao_Bronze_1_tS3Put_1 = field_Processos_Ingestao_Bronze_1_tS3Put_1
								.get(standalone_Processos_Ingestao_Bronze_1_tS3Put_1);
						Object con_Processos_Ingestao_Bronze_1_tS3Put_1 = globalMap
								.get("conn_Processos_Ingestao_Bronze_1_tS3Connection_2");
						if (con_Processos_Ingestao_Bronze_1_tS3Put_1 == null) {
							throw new RuntimeException("can't find the connection object");
						}

						Class<?> current_Processos_Ingestao_Bronze_1_tS3Put_1 = v_Processos_Ingestao_Bronze_1_tS3Put_1
								.getClass();
						while (current_Processos_Ingestao_Bronze_1_tS3Put_1 != null
								&& current_Processos_Ingestao_Bronze_1_tS3Put_1 != Object.class) {
							java.util.stream.Stream.of(current_Processos_Ingestao_Bronze_1_tS3Put_1.getDeclaredFields())
									.filter(f_Processos_Ingestao_Bronze_1_tS3Put_1 -> f_Processos_Ingestao_Bronze_1_tS3Put_1
											.isAnnotationPresent(
													org.talend.sdk.component.api.service.connection.Connection.class))
									.forEach(f_Processos_Ingestao_Bronze_1_tS3Put_1 -> {
										if (!f_Processos_Ingestao_Bronze_1_tS3Put_1.isAccessible()) {
											f_Processos_Ingestao_Bronze_1_tS3Put_1.setAccessible(true);
										}
										try {
											f_Processos_Ingestao_Bronze_1_tS3Put_1.set(
													v_Processos_Ingestao_Bronze_1_tS3Put_1,
													con_Processos_Ingestao_Bronze_1_tS3Put_1);
										} catch (final IllegalAccessException e_Processos_Ingestao_Bronze_1_tS3Put_1) {
											throw new IllegalStateException(e_Processos_Ingestao_Bronze_1_tS3Put_1);
										}
									});
							current_Processos_Ingestao_Bronze_1_tS3Put_1 = current_Processos_Ingestao_Bronze_1_tS3Put_1
									.getSuperclass();
						}
					} catch (Exception e_Processos_Ingestao_Bronze_1_tS3Put_1) {
						throw e_Processos_Ingestao_Bronze_1_tS3Put_1;
					}

					standalone_Processos_Ingestao_Bronze_1_tS3Put_1.start();
					globalMap.put("standalone_Processos_Ingestao_Bronze_1_tS3Put_1",
							standalone_Processos_Ingestao_Bronze_1_tS3Put_1);

					standalone_Processos_Ingestao_Bronze_1_tS3Put_1.runAtDriver();
//Standalone begin stub

					/**
					 * [Processos_Ingestao_Bronze_1_tS3Put_1 begin ] stop
					 */

					/**
					 * [Processos_Ingestao_Bronze_1_tS3Put_1 main ] start
					 */

					s(currentComponent = "Processos_Ingestao_Bronze_1_tS3Put_1");

					tos_count_Processos_Ingestao_Bronze_1_tS3Put_1++;

					/**
					 * [Processos_Ingestao_Bronze_1_tS3Put_1 main ] stop
					 */

					/**
					 * [Processos_Ingestao_Bronze_1_tS3Put_1 process_data_begin ] start
					 */

					s(currentComponent = "Processos_Ingestao_Bronze_1_tS3Put_1");

					/**
					 * [Processos_Ingestao_Bronze_1_tS3Put_1 process_data_begin ] stop
					 */

					/**
					 * [Processos_Ingestao_Bronze_1_tS3Put_1 process_data_end ] start
					 */

					s(currentComponent = "Processos_Ingestao_Bronze_1_tS3Put_1");

					/**
					 * [Processos_Ingestao_Bronze_1_tS3Put_1 process_data_end ] stop
					 */

					/**
					 * [Processos_Ingestao_Bronze_1_tS3Put_1 end ] start
					 */

					s(currentComponent = "Processos_Ingestao_Bronze_1_tS3Put_1");

					if (standalone_Processos_Ingestao_Bronze_1_tS3Put_1 != null) {
						standalone_Processos_Ingestao_Bronze_1_tS3Put_1.stop();
					}

					globalMap.remove("standalone_Processos_Ingestao_Bronze_1_tS3Put_1");

					if (log.isDebugEnabled())
						log.debug("Processos_Ingestao_Bronze_1_tS3Put_1 - " + ("Done."));

					ok_Hash.put("Processos_Ingestao_Bronze_1_tS3Put_1", true);
					end_Hash.put("Processos_Ingestao_Bronze_1_tS3Put_1", System.currentTimeMillis());

					/**
					 * [Processos_Ingestao_Bronze_1_tS3Put_1 end ] stop
					 */

					if (execStat) {
						runStat.updateStatOnConnection("Processos_Ingestao_Bronze_1_iterate1", 2,
								"exec" + NB_ITERATE_Processos_Ingestao_Bronze_1_tS3Put_1);
					}

					/**
					 * [Processos_Ingestao_Bronze_1_tFileList_1 process_data_end ] start
					 */

					s(currentComponent = "Processos_Ingestao_Bronze_1_tFileList_1");

					/**
					 * [Processos_Ingestao_Bronze_1_tFileList_1 process_data_end ] stop
					 */

					/**
					 * [Processos_Ingestao_Bronze_1_tFileList_1 end ] start
					 */

					s(currentComponent = "Processos_Ingestao_Bronze_1_tFileList_1");

				}
				globalMap.put("Processos_Ingestao_Bronze_1_tFileList_1_NB_FILE",
						NB_FILEProcessos_Ingestao_Bronze_1_tFileList_1);

				log.info("Processos_Ingestao_Bronze_1_tFileList_1 - File or directory count : "
						+ NB_FILEProcessos_Ingestao_Bronze_1_tFileList_1);

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tFileList_1 - " + ("Done."));

				ok_Hash.put("Processos_Ingestao_Bronze_1_tFileList_1", true);
				end_Hash.put("Processos_Ingestao_Bronze_1_tFileList_1", System.currentTimeMillis());

				/**
				 * [Processos_Ingestao_Bronze_1_tFileList_1 end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException) && !(e instanceof TDieException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [Processos_Ingestao_Bronze_1_tFileList_1 finally ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileList_1");

				/**
				 * [Processos_Ingestao_Bronze_1_tFileList_1 finally ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tS3Put_1 finally ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tS3Put_1");

				final org.talend.sdk.component.runtime.standalone.DriverRunner standalone_Processos_Ingestao_Bronze_1_tS3Put_1 = org.talend.sdk.component.runtime.standalone.DriverRunner.class
						.cast(globalMap.remove("standalone_Processos_Ingestao_Bronze_1_tS3Put_1"));
				try {
					if (standalone_Processos_Ingestao_Bronze_1_tS3Put_1 != null) {
						standalone_Processos_Ingestao_Bronze_1_tS3Put_1.stop();
					}
				} catch (final RuntimeException re) {
					throw new TalendException(re, currentComponent, cLabel, globalMap);
				}

				/**
				 * [Processos_Ingestao_Bronze_1_tS3Put_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("Processos_Ingestao_Bronze_1_tFileList_1_SUBPROCESS_STATE", 1);
	}

	public static class Processos_Ingestao_Bronze_1_row3Struct
			implements routines.system.IPersistableRow<Processos_Ingestao_Bronze_1_row3Struct> {
		final static byte[] commonByteArrayLock_DEMOTRF5_TRF5 = new byte[0];
		static byte[] commonByteArray_DEMOTRF5_TRF5 = new byte[0];

		public int id_classe;

		public int getId_classe() {
			return this.id_classe;
		}

		public Boolean id_classeIsNullable() {
			return false;
		}

		public Boolean id_classeIsKey() {
			return false;
		}

		public Integer id_classeLength() {
			return 10;
		}

		public Integer id_classePrecision() {
			return 0;
		}

		public String id_classeDefault() {

			return "";

		}

		public String id_classeComment() {

			return "";

		}

		public String id_classePattern() {

			return "";

		}

		public String id_classeOriginalDbColumnName() {

			return "id_classe";

		}

		public String cod_cnj;

		public String getCod_cnj() {
			return this.cod_cnj;
		}

		public Boolean cod_cnjIsNullable() {
			return true;
		}

		public Boolean cod_cnjIsKey() {
			return false;
		}

		public Integer cod_cnjLength() {
			return 20;
		}

		public Integer cod_cnjPrecision() {
			return 0;
		}

		public String cod_cnjDefault() {

			return null;

		}

		public String cod_cnjComment() {

			return "";

		}

		public String cod_cnjPattern() {

			return "";

		}

		public String cod_cnjOriginalDbColumnName() {

			return "cod_cnj";

		}

		public String nome_classe;

		public String getNome_classe() {
			return this.nome_classe;
		}

		public Boolean nome_classeIsNullable() {
			return true;
		}

		public Boolean nome_classeIsKey() {
			return false;
		}

		public Integer nome_classeLength() {
			return 255;
		}

		public Integer nome_classePrecision() {
			return 0;
		}

		public String nome_classeDefault() {

			return null;

		}

		public String nome_classeComment() {

			return "";

		}

		public String nome_classePattern() {

			return "";

		}

		public String nome_classeOriginalDbColumnName() {

			return "nome_classe";

		}

		public String tipo_procedimento;

		public String getTipo_procedimento() {
			return this.tipo_procedimento;
		}

		public Boolean tipo_procedimentoIsNullable() {
			return true;
		}

		public Boolean tipo_procedimentoIsKey() {
			return false;
		}

		public Integer tipo_procedimentoLength() {
			return 50;
		}

		public Integer tipo_procedimentoPrecision() {
			return 0;
		}

		public String tipo_procedimentoDefault() {

			return null;

		}

		public String tipo_procedimentoComment() {

			return "";

		}

		public String tipo_procedimentoPattern() {

			return "";

		}

		public String tipo_procedimentoOriginalDbColumnName() {

			return "tipo_procedimento";

		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_DEMOTRF5_TRF5.length) {
					if (length < 1024 && commonByteArray_DEMOTRF5_TRF5.length == 0) {
						commonByteArray_DEMOTRF5_TRF5 = new byte[1024];
					} else {
						commonByteArray_DEMOTRF5_TRF5 = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_DEMOTRF5_TRF5, 0, length);
				strReturn = new String(commonByteArray_DEMOTRF5_TRF5, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			String strReturn = null;
			int length = 0;
			length = unmarshaller.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_DEMOTRF5_TRF5.length) {
					if (length < 1024 && commonByteArray_DEMOTRF5_TRF5.length == 0) {
						commonByteArray_DEMOTRF5_TRF5 = new byte[1024];
					} else {
						commonByteArray_DEMOTRF5_TRF5 = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_DEMOTRF5_TRF5, 0, length);
				strReturn = new String(commonByteArray_DEMOTRF5_TRF5, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos) throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (str == null) {
				marshaller.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				marshaller.writeInt(byteArray.length);
				marshaller.write(byteArray);
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_DEMOTRF5_TRF5) {

				try {

					int length = 0;

					this.id_classe = dis.readInt();

					this.cod_cnj = readString(dis);

					this.nome_classe = readString(dis);

					this.tipo_procedimento = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_DEMOTRF5_TRF5) {

				try {

					int length = 0;

					this.id_classe = dis.readInt();

					this.cod_cnj = readString(dis);

					this.nome_classe = readString(dis);

					this.tipo_procedimento = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// int

				dos.writeInt(this.id_classe);

				// String

				writeString(this.cod_cnj, dos);

				// String

				writeString(this.nome_classe, dos);

				// String

				writeString(this.tipo_procedimento, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// int

				dos.writeInt(this.id_classe);

				// String

				writeString(this.cod_cnj, dos);

				// String

				writeString(this.nome_classe, dos);

				// String

				writeString(this.tipo_procedimento, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("id_classe=" + String.valueOf(id_classe));
			sb.append(",cod_cnj=" + cod_cnj);
			sb.append(",nome_classe=" + nome_classe);
			sb.append(",tipo_procedimento=" + tipo_procedimento);
			sb.append("]");

			return sb.toString();
		}

		public String toLogString() {
			StringBuilder sb = new StringBuilder();

			sb.append(id_classe);

			sb.append("|");

			if (cod_cnj == null) {
				sb.append("<null>");
			} else {
				sb.append(cod_cnj);
			}

			sb.append("|");

			if (nome_classe == null) {
				sb.append("<null>");
			} else {
				sb.append(nome_classe);
			}

			sb.append("|");

			if (tipo_procedimento == null) {
				sb.append("<null>");
			} else {
				sb.append(tipo_procedimento);
			}

			sb.append("|");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(Processos_Ingestao_Bronze_1_row3Struct other) {

			int returnValue = -1;

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(), object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public void Processos_Ingestao_Bronze_1_tDBInput_3Process(final java.util.Map<String, Object> globalMap)
			throws TalendException {
		globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_3_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("Processos_Ingestao_Bronze_1_tDBInput_3", "wkb6BJ_");

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				Processos_Ingestao_Bronze_1_row3Struct Processos_Ingestao_Bronze_1_row3 = new Processos_Ingestao_Bronze_1_row3Struct();

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_3 begin ] start
				 */

				sh("Processos_Ingestao_Bronze_1_tFileOutputParquet_3");

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_3");

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0,
						"Processos_Ingestao_Bronze_1_row3");

				int tos_count_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = 0;

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_3 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = new StringBuilder();
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_3.append("Parameters:");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_3.append("FILENAME" + " = "
									+ "\"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/classes_processuais_bronze\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
									.append("FILE_ACTION" + " = " + "OVERWRITE");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
									.append("COMPRESSION" + " = " + "\"UNCOMPRESSED\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
									.append("USE_EXTERNAL_HADOOP_DEPS" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
									.append("ROW_GROUP_SIZE" + " = " + "134217728");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
									.append("PAGE_SIZE" + " = " + "1048576");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_3.append(" | ");
							if (log.isDebugEnabled())
								log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_3 - "
										+ (log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_3));
						}
					}
					new BytesLimit65535_Processos_Ingestao_Bronze_1_tFileOutputParquet_3().limitLog4jByte();
				}
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter(
								final Processos_Ingestao_Bronze_1_row3Struct Processos_Ingestao_Bronze_1_row3)
								throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("FILENAME", String.valueOf(
										"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/classes_processuais_bronze"));
								component_parameters.put("FILE_ACTION", String.valueOf("OVERWRITE"));
								component_parameters.put("COMPRESSION", String.valueOf("UNCOMPRESSED"));
								component_parameters.put("USE_EXTERNAL_HADOOP_DEPS", String.valueOf("false"));
								component_parameters.put("ROW_GROUP_SIZE", String.valueOf("134217728"));
								component_parameters.put("PAGE_SIZE", String.valueOf("1048576"));

							} catch (java.lang.Exception e_Processos_Ingestao_Bronze_1_tFileOutputParquet_3) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("Processos_Ingestao_Bronze_1_tFileOutputParquet_3",
							"tFileOutputParquet", new ParameterUtil_Processos_Ingestao_Bronze_1_tFileOutputParquet_3()
									.getParameter(Processos_Ingestao_Bronze_1_row3));
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("Processos_Ingestao_Bronze_1_tFileOutputParquet_3", "tFileOutputParquet_3",
							"tFileOutputParquet");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				int nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = 0;
				org.apache.parquet.schema.Types.MessageTypeBuilder schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = org.apache.parquet.schema.Types
						.buildMessage();
				org.apache.parquet.schema.MessageType messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = null;
				org.talend.parquet.data.simple.SimpleGroupFactory factory_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = null;
				org.apache.parquet.hadoop.ParquetWriter<org.talend.parquet.data.Group> writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = null;

				String filePath_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = "C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/classes_processuais_bronze";
				globalMap.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_3_FILE_PATH",
						filePath_Processos_Ingestao_Bronze_1_tFileOutputParquet_3);
				org.apache.hadoop.conf.Configuration config_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = new org.apache.hadoop.conf.Configuration();
				config_Processos_Ingestao_Bronze_1_tFileOutputParquet_3.set("fs.file.impl",
						org.apache.hadoop.fs.LocalFileSystem.class.getName());
				org.apache.hadoop.fs.Path path_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = new org.apache.hadoop.fs.Path(
						filePath_Processos_Ingestao_Bronze_1_tFileOutputParquet_3);
				// CRC file path
				String crcName_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = "."
						+ path_Processos_Ingestao_Bronze_1_tFileOutputParquet_3.getName() + ".crc";
				org.apache.hadoop.fs.Path crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = new org.apache.hadoop.fs.Path(
						path_Processos_Ingestao_Bronze_1_tFileOutputParquet_3.getParent(),
						crcName_Processos_Ingestao_Bronze_1_tFileOutputParquet_3);
				String compressName_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = "UNCOMPRESSED";
				int rowGroupSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = 134217728;
				int pageSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = 1048576;
				java.util.Map<String, org.talend.parquet.data.simple.SimpleGroupFactory> cachedFactory_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = new java.util.HashMap<>();
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("id_classe", false,
								"INT32", "INT_32"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("cod_cnj", true,
								"BINARY", "UTF8"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("nome_classe", true,
								"BINARY", "UTF8"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("tipo_procedimento",
								true, "BINARY", "UTF8"));
				messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
						.named("Schema");

				org.talend.parquet.hadoop.TalendGroupWriteSupport.setSchema(
						messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_3,
						config_Processos_Ingestao_Bronze_1_tFileOutputParquet_3);
				factory_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = new org.talend.parquet.data.simple.SimpleGroupFactory(
						messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_3);
				org.apache.parquet.hadoop.ParquetWriter.Builder<org.talend.parquet.data.Group, org.talend.parquet.hadoop.TalendParquetWriter.Builder> builder_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = org.talend.parquet.hadoop.TalendParquetWriter
						.builder(org.apache.parquet.hadoop.util.HadoopOutputFile.fromPath(
								path_Processos_Ingestao_Bronze_1_tFileOutputParquet_3,
								config_Processos_Ingestao_Bronze_1_tFileOutputParquet_3));
				builder_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
						.withWriteMode(org.apache.parquet.hadoop.ParquetFileWriter.Mode.OVERWRITE)
						.withCompressionCodec(org.apache.parquet.hadoop.metadata.CompressionCodecName
								.fromConf(compressName_Processos_Ingestao_Bronze_1_tFileOutputParquet_3))
						.withRowGroupSize(rowGroupSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_3)
						.withPageSize(pageSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_3)
						.withConf(config_Processos_Ingestao_Bronze_1_tFileOutputParquet_3);

				writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = builder_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
						.build();

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_3 begin ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_3 begin ] start
				 */

				sh("Processos_Ingestao_Bronze_1_tDBInput_3");

				s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_3");

				int tos_count_Processos_Ingestao_Bronze_1_tDBInput_3 = 0;

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tDBInput_3 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_Processos_Ingestao_Bronze_1_tDBInput_3 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3 = new StringBuilder();
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append("Parameters:");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3
									.append("USE_EXISTING_CONNECTION" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append("DB_VERSION" + " = " + "V9_X");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append("HOST" + " = "
									+ "\"peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append("PORT" + " = " + "\"5432\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3
									.append("DBNAME" + " = " + "\"qlik_demo\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3
									.append("SCHEMA_DB" + " = " + "\"public\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3
									.append("USER" + " = " + "\"peta_qlik\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append("PASS" + " = "
									+ String.valueOf(
											"enc:routine.encryption.key.v1:dgPLOHwJp73Lz4+izJzvaa5i5fizFODh2s0bEa0k1nG3E/iUKA==")
											.substring(0, 4)
									+ "...");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append("QUERYSTORE" + " = " + "\"\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append("QUERY" + " = "
									+ "\"SELECT    \\\"public\\\".\\\"classes_processuais\\\".\\\"id_classe\\\",    \\\"public\\\".\\\"classes_processuais\\\".\\\"cod_cnj\\\",    \\\"public\\\".\\\"classes_processuais\\\".\\\"nome_classe\\\",    \\\"public\\\".\\\"classes_processuais\\\".\\\"tipo_procedimento\\\"  FROM \\\"public\\\".\\\"classes_processuais\\\"\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3
									.append("SPECIFY_DATASOURCE_ALIAS" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3
									.append("PROPERTIES" + " = " + "\"classe\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3
									.append("USE_CURSOR" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3
									.append("TRIM_ALL_COLUMN" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(
									"TRIM_COLUMN" + " = " + "[{TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("id_classe")
											+ "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("cod_cnj") + "}, {TRIM="
											+ ("false") + ", SCHEMA_COLUMN=" + ("nome_classe") + "}, {TRIM=" + ("false")
											+ ", SCHEMA_COLUMN=" + ("tipo_procedimento") + "}]");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3
									.append("UNIFIED_COMPONENTS" + " = " + "tPostgresqlInput");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3.append(" | ");
							if (log.isDebugEnabled())
								log.debug("Processos_Ingestao_Bronze_1_tDBInput_3 - "
										+ (log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_3));
						}
					}
					new BytesLimit65535_Processos_Ingestao_Bronze_1_tDBInput_3().limitLog4jByte();
				}
				boolean init_Processos_Ingestao_Bronze_1_tDBInput_3_0 = true;
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_Processos_Ingestao_Bronze_1_tDBInput_3 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter() throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("USE_EXISTING_CONNECTION", String.valueOf("false"));
								component_parameters.put("DB_VERSION", String.valueOf("V9_X"));
								component_parameters.put("HOST", String
										.valueOf("peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com"));
								component_parameters.put("PORT", String.valueOf("5432"));
								component_parameters.put("DBNAME", String.valueOf("qlik_demo"));
								component_parameters.put("SCHEMA_DB", String.valueOf("public"));
								component_parameters.put("USER", String.valueOf("peta_qlik"));
								component_parameters.put("QUERYSTORE", String.valueOf(""));
								component_parameters.put("QUERY", String.valueOf(new StringBuilder().append(
										"SELECT \n  \"public\".\"classes_processuais\".\"id_classe\", \n  \"public\".\"classes_processuais\".\"cod_cnj\", \n  \"pub"
												+ "lic\".\"classes_processuais\".\"nome_classe\", \n  \"public\".\"classes_processuais\".\"tipo_procedimento\"\n FROM \"publi"
												+ "c\".\"classes_processuais\"")
										.toString()));
								component_parameters.put("SPECIFY_DATASOURCE_ALIAS", String.valueOf("false"));
								component_parameters.put("PROPERTIES", String.valueOf("classe"));
								component_parameters.put("USE_CURSOR", String.valueOf("false"));
								component_parameters.put("TRIM_ALL_COLUMN", String.valueOf("false"));
								component_parameters.put("TRIM_COLUMN",
										String.valueOf(new StringBuilder().append("[{TRIM=").append("false")
												.append(", SCHEMA_COLUMN=").append("id_classe").append("}, {TRIM=")
												.append("false").append(", SCHEMA_COLUMN=").append("cod_cnj")
												.append("}, {TRIM=").append("false").append(", SCHEMA_COLUMN=")
												.append("nome_classe").append("}, {TRIM=").append("false")
												.append(", SCHEMA_COLUMN=").append("tipo_procedimento").append("}]")
												.toString()));
								component_parameters.put("UNIFIED_COMPONENTS", String.valueOf("tPostgresqlInput"));

							} catch (java.lang.Exception e_Processos_Ingestao_Bronze_1_tDBInput_3) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("Processos_Ingestao_Bronze_1_tDBInput_3",
							"tPostgresqlInput",
							new ParameterUtil_Processos_Ingestao_Bronze_1_tDBInput_3().getParameter());
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("Processos_Ingestao_Bronze_1_tDBInput_3", "tDBInput_3", "tPostgresqlInput");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				int nb_line_Processos_Ingestao_Bronze_1_tDBInput_3 = 0;
				java.sql.Connection conn_Processos_Ingestao_Bronze_1_tDBInput_3 = null;
				String driverClass_Processos_Ingestao_Bronze_1_tDBInput_3 = "org.postgresql.Driver";
				java.lang.Class jdbcclazz_Processos_Ingestao_Bronze_1_tDBInput_3 = java.lang.Class
						.forName(driverClass_Processos_Ingestao_Bronze_1_tDBInput_3);
				String dbUser_Processos_Ingestao_Bronze_1_tDBInput_3 = "peta_qlik";

				final String decryptedPassword_Processos_Ingestao_Bronze_1_tDBInput_3 = java.util.Optional
						.ofNullable(routines.system.PasswordEncryptUtil.decryptPassword(
								"enc:routine.encryption.key.v1:aAtIfyUwKPElzvLk8g38NGoVLF1VTtTgsQDmcG1NpW67077+RA=="))
						.orElse("");

				String dbPwd_Processos_Ingestao_Bronze_1_tDBInput_3 = decryptedPassword_Processos_Ingestao_Bronze_1_tDBInput_3;

				String url_Processos_Ingestao_Bronze_1_tDBInput_3 = "jdbc:postgresql://"
						+ "peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com" + ":" + "5432" + "/"
						+ "qlik_demo" + "?" + "classe";

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_3 - Driver ClassName: "
						+ driverClass_Processos_Ingestao_Bronze_1_tDBInput_3 + ".");

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_3 - Connection attempt to '"
						+ url_Processos_Ingestao_Bronze_1_tDBInput_3.replaceAll("(?<=trustStorePassword=)[^;]*",
								"********")
						+ "' with the username '" + dbUser_Processos_Ingestao_Bronze_1_tDBInput_3 + "'.");

				conn_Processos_Ingestao_Bronze_1_tDBInput_3 = java.sql.DriverManager.getConnection(
						url_Processos_Ingestao_Bronze_1_tDBInput_3, dbUser_Processos_Ingestao_Bronze_1_tDBInput_3,
						dbPwd_Processos_Ingestao_Bronze_1_tDBInput_3);
				log.debug("Processos_Ingestao_Bronze_1_tDBInput_3 - Connection to '"
						+ url_Processos_Ingestao_Bronze_1_tDBInput_3.replaceAll("(?<=trustStorePassword=)[^;]*",
								"********")
						+ "' has succeeded.");

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_3 - Connection is set auto commit to 'false'.");

				conn_Processos_Ingestao_Bronze_1_tDBInput_3.setAutoCommit(false);

				java.sql.Statement stmt_Processos_Ingestao_Bronze_1_tDBInput_3 = conn_Processos_Ingestao_Bronze_1_tDBInput_3
						.createStatement();

				String dbquery_Processos_Ingestao_Bronze_1_tDBInput_3 = new StringBuilder().append(
						"SELECT \n  \"public\".\"classes_processuais\".\"id_classe\", \n  \"public\".\"classes_processuais\".\"cod_cnj\", \n  \"pub"
								+ "lic\".\"classes_processuais\".\"nome_classe\", \n  \"public\".\"classes_processuais\".\"tipo_procedimento\"\n FROM \"publi"
								+ "c\".\"classes_processuais\"")
						.toString();

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_3 - Executing the query: '"
						+ dbquery_Processos_Ingestao_Bronze_1_tDBInput_3 + "'.");

				globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_3_QUERY",
						dbquery_Processos_Ingestao_Bronze_1_tDBInput_3);

				java.sql.ResultSet rs_Processos_Ingestao_Bronze_1_tDBInput_3 = null;

				try {
					rs_Processos_Ingestao_Bronze_1_tDBInput_3 = stmt_Processos_Ingestao_Bronze_1_tDBInput_3
							.executeQuery(dbquery_Processos_Ingestao_Bronze_1_tDBInput_3);
					java.sql.ResultSetMetaData rsmd_Processos_Ingestao_Bronze_1_tDBInput_3 = rs_Processos_Ingestao_Bronze_1_tDBInput_3
							.getMetaData();
					int colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_3 = rsmd_Processos_Ingestao_Bronze_1_tDBInput_3
							.getColumnCount();

					String tmpContent_Processos_Ingestao_Bronze_1_tDBInput_3 = null;

					log.debug("Processos_Ingestao_Bronze_1_tDBInput_3 - Retrieving records from the database.");

					while (rs_Processos_Ingestao_Bronze_1_tDBInput_3.next()) {
						nb_line_Processos_Ingestao_Bronze_1_tDBInput_3++;

						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_3 < 1) {
							Processos_Ingestao_Bronze_1_row3.id_classe = 0;
						} else {

							Processos_Ingestao_Bronze_1_row3.id_classe = rs_Processos_Ingestao_Bronze_1_tDBInput_3
									.getInt(1);
							if (rs_Processos_Ingestao_Bronze_1_tDBInput_3.wasNull()) {
								throw new RuntimeException("Null value in non-Nullable column");
							}
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_3 < 2) {
							Processos_Ingestao_Bronze_1_row3.cod_cnj = null;
						} else {

							Processos_Ingestao_Bronze_1_row3.cod_cnj = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_3, 2, false);
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_3 < 3) {
							Processos_Ingestao_Bronze_1_row3.nome_classe = null;
						} else {

							Processos_Ingestao_Bronze_1_row3.nome_classe = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_3, 3, false);
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_3 < 4) {
							Processos_Ingestao_Bronze_1_row3.tipo_procedimento = null;
						} else {

							Processos_Ingestao_Bronze_1_row3.tipo_procedimento = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_3, 4, false);
						}

						log.debug("Processos_Ingestao_Bronze_1_tDBInput_3 - Retrieving the record "
								+ nb_line_Processos_Ingestao_Bronze_1_tDBInput_3 + ".");

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_3 begin ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_3 main ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_3");

						// QTUP-3575
						if (enableLineage && init_Processos_Ingestao_Bronze_1_tDBInput_3_0) {
							class SchemaUtil_Processos_Ingestao_Bronze_1_row3 {

								private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
									java.util.Map<String, String> field = new java.util.HashMap<>();
									field.put("name", values[0]);
									field.put("origin_name", values[1]);
									field.put("iskey", values[2]);
									field.put("talend_type", values[3]);
									field.put("type", values[4]);
									field.put("nullable", values[5]);
									field.put("pattern", values[6]);
									field.put("length", values[7]);
									field.put("precision", values[8]);
									schema.add(field);
								}

								public java.util.List<java.util.Map<String, String>> getSchema(
										final Processos_Ingestao_Bronze_1_row3Struct Processos_Ingestao_Bronze_1_row3) {
									java.util.List<java.util.Map<String, String>> s = new java.util.ArrayList<>();
									if (Processos_Ingestao_Bronze_1_row3 == null) {
										return s;
									}
									a(s, "id_classe", "id_classe", "false", "id_Integer", "SERIAL", "false", "", "10",
											"0");
									a(s, "cod_cnj", "cod_cnj", "false", "id_String", "VARCHAR", "true", "", "20", "0");
									a(s, "nome_classe", "nome_classe", "false", "id_String", "VARCHAR", "true", "",
											"255", "0");
									a(s, "tipo_procedimento", "tipo_procedimento", "false", "id_String", "VARCHAR",
											"true", "", "50", "0");
									return s;
								}

							}

							if (Processos_Ingestao_Bronze_1_row3 != null) {
								talendJobLog.addConnectionSchemaMessage("Processos_Ingestao_Bronze_1_tDBInput_3",
										"tPostgresqlInput", "Processos_Ingestao_Bronze_1_tFileOutputParquet_3",
										"tFileOutputParquet", "Processos_Ingestao_Bronze_1_row3" + iterateId,
										new SchemaUtil_Processos_Ingestao_Bronze_1_row3()
												.getSchema(Processos_Ingestao_Bronze_1_row3));
								talendJobLogProcess(globalMap);
								init_Processos_Ingestao_Bronze_1_tDBInput_3_0 = false;
							}

						}
						// QTUP-3575

						tos_count_Processos_Ingestao_Bronze_1_tDBInput_3++;

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_3 main ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_3 process_data_begin ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_3");

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_3 process_data_begin ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_3 main ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_3");

						if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

								, "Processos_Ingestao_Bronze_1_row3", "Processos_Ingestao_Bronze_1_tDBInput_3",
								"tDBInput_3", "tPostgresqlInput", "Processos_Ingestao_Bronze_1_tFileOutputParquet_3",
								"tFileOutputParquet_3", "tFileOutputParquet"

						)) {
							talendJobLogProcess(globalMap);
						}

						if (log.isTraceEnabled()) {
							log.trace("Processos_Ingestao_Bronze_1_row3 - "
									+ (Processos_Ingestao_Bronze_1_row3 == null ? ""
											: Processos_Ingestao_Bronze_1_row3.toLogString()));
						}

						org.talend.parquet.data.Group group_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = factory_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
								.newGroup();

						group_Processos_Ingestao_Bronze_1_tFileOutputParquet_3.append("id_classe",
								Processos_Ingestao_Bronze_1_row3.id_classe);
						if (Processos_Ingestao_Bronze_1_row3.cod_cnj != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_3.append("cod_cnj",
									String.valueOf(Processos_Ingestao_Bronze_1_row3.cod_cnj));
						}

						if (Processos_Ingestao_Bronze_1_row3.nome_classe != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_3.append("nome_classe",
									String.valueOf(Processos_Ingestao_Bronze_1_row3.nome_classe));
						}

						if (Processos_Ingestao_Bronze_1_row3.tipo_procedimento != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_3.append("tipo_procedimento",
									String.valueOf(Processos_Ingestao_Bronze_1_row3.tipo_procedimento));
						}

						writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
								.write(group_Processos_Ingestao_Bronze_1_tFileOutputParquet_3);
						nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_3++;
						log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_3 - Writing the record "
								+ nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 + " to the file.");

						tos_count_Processos_Ingestao_Bronze_1_tFileOutputParquet_3++;

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_3 main ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_3 process_data_begin ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_3");

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_3 process_data_begin ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_3 process_data_end ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_3");

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_3 process_data_end ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_3 process_data_end ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_3");

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_3 process_data_end ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_3 end ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_3");

					}
				} finally {
					if (rs_Processos_Ingestao_Bronze_1_tDBInput_3 != null) {
						rs_Processos_Ingestao_Bronze_1_tDBInput_3.close();
					}
					if (stmt_Processos_Ingestao_Bronze_1_tDBInput_3 != null) {
						stmt_Processos_Ingestao_Bronze_1_tDBInput_3.close();
					}
					if (conn_Processos_Ingestao_Bronze_1_tDBInput_3 != null
							&& !conn_Processos_Ingestao_Bronze_1_tDBInput_3.isClosed()) {

						log.debug("Processos_Ingestao_Bronze_1_tDBInput_3 - Closing the connection to the database.");

						conn_Processos_Ingestao_Bronze_1_tDBInput_3.close();

						if ("com.mysql.cj.jdbc.Driver".equals((String) globalMap.get("driverClass_"))
								&& routines.system.BundleUtils.inOSGi()) {
							Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread")
									.getMethod("checkedShutdown").invoke(null, (Object[]) null);
						}

						log.debug("Processos_Ingestao_Bronze_1_tDBInput_3 - Connection to the database closed.");

					}

				}
				globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_3_NB_LINE",
						nb_line_Processos_Ingestao_Bronze_1_tDBInput_3);
				log.debug("Processos_Ingestao_Bronze_1_tDBInput_3 - Retrieved records count: "
						+ nb_line_Processos_Ingestao_Bronze_1_tDBInput_3 + " .");

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tDBInput_3 - " + ("Done."));

				ok_Hash.put("Processos_Ingestao_Bronze_1_tDBInput_3", true);
				end_Hash.put("Processos_Ingestao_Bronze_1_tDBInput_3", System.currentTimeMillis());

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_3 end ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_3 end ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_3");

				globalMap.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_3_NB_LINE",
						nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_3);

				log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_3 - Written records count: "
						+ nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 + " .");

				if (writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 != null) {
					writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_3.close();
				}
				org.apache.hadoop.fs.FileSystem fs_Processos_Ingestao_Bronze_1_tFileOutputParquet_3 = crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
						.getFileSystem(config_Processos_Ingestao_Bronze_1_tFileOutputParquet_3);
				if (fs_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
						.exists(crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_3)) {
					fs_Processos_Ingestao_Bronze_1_tFileOutputParquet_3
							.delete(crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_3, false);
				}

				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId,
						"Processos_Ingestao_Bronze_1_row3", 2, 0, "Processos_Ingestao_Bronze_1_tDBInput_3",
						"tDBInput_3", "tPostgresqlInput", "Processos_Ingestao_Bronze_1_tFileOutputParquet_3",
						"tFileOutputParquet_3", "tFileOutputParquet", "output")) {
					talendJobLogProcess(globalMap);
				}

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_3 - " + ("Done."));

				ok_Hash.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_3", true);
				end_Hash.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_3", System.currentTimeMillis());

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_3 end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException) && !(e instanceof TDieException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_3 finally ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_3");

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_3 finally ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_3 finally ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_3");

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_3 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_3_SUBPROCESS_STATE", 1);
	}

	public static class Processos_Ingestao_Bronze_1_row4Struct
			implements routines.system.IPersistableRow<Processos_Ingestao_Bronze_1_row4Struct> {
		final static byte[] commonByteArrayLock_DEMOTRF5_TRF5 = new byte[0];
		static byte[] commonByteArray_DEMOTRF5_TRF5 = new byte[0];

		public int id_assunto;

		public int getId_assunto() {
			return this.id_assunto;
		}

		public Boolean id_assuntoIsNullable() {
			return false;
		}

		public Boolean id_assuntoIsKey() {
			return false;
		}

		public Integer id_assuntoLength() {
			return 10;
		}

		public Integer id_assuntoPrecision() {
			return 0;
		}

		public String id_assuntoDefault() {

			return "";

		}

		public String id_assuntoComment() {

			return "";

		}

		public String id_assuntoPattern() {

			return "";

		}

		public String id_assuntoOriginalDbColumnName() {

			return "id_assunto";

		}

		public String cod_assunto_cnj;

		public String getCod_assunto_cnj() {
			return this.cod_assunto_cnj;
		}

		public Boolean cod_assunto_cnjIsNullable() {
			return true;
		}

		public Boolean cod_assunto_cnjIsKey() {
			return false;
		}

		public Integer cod_assunto_cnjLength() {
			return 20;
		}

		public Integer cod_assunto_cnjPrecision() {
			return 0;
		}

		public String cod_assunto_cnjDefault() {

			return null;

		}

		public String cod_assunto_cnjComment() {

			return "";

		}

		public String cod_assunto_cnjPattern() {

			return "";

		}

		public String cod_assunto_cnjOriginalDbColumnName() {

			return "cod_assunto_cnj";

		}

		public String descricao_assunto;

		public String getDescricao_assunto() {
			return this.descricao_assunto;
		}

		public Boolean descricao_assuntoIsNullable() {
			return true;
		}

		public Boolean descricao_assuntoIsKey() {
			return false;
		}

		public Integer descricao_assuntoLength() {
			return 2147483647;
		}

		public Integer descricao_assuntoPrecision() {
			return 0;
		}

		public String descricao_assuntoDefault() {

			return null;

		}

		public String descricao_assuntoComment() {

			return "";

		}

		public String descricao_assuntoPattern() {

			return "";

		}

		public String descricao_assuntoOriginalDbColumnName() {

			return "descricao_assunto";

		}

		public String ramo_direito;

		public String getRamo_direito() {
			return this.ramo_direito;
		}

		public Boolean ramo_direitoIsNullable() {
			return true;
		}

		public Boolean ramo_direitoIsKey() {
			return false;
		}

		public Integer ramo_direitoLength() {
			return 100;
		}

		public Integer ramo_direitoPrecision() {
			return 0;
		}

		public String ramo_direitoDefault() {

			return null;

		}

		public String ramo_direitoComment() {

			return "";

		}

		public String ramo_direitoPattern() {

			return "";

		}

		public String ramo_direitoOriginalDbColumnName() {

			return "ramo_direito";

		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_DEMOTRF5_TRF5.length) {
					if (length < 1024 && commonByteArray_DEMOTRF5_TRF5.length == 0) {
						commonByteArray_DEMOTRF5_TRF5 = new byte[1024];
					} else {
						commonByteArray_DEMOTRF5_TRF5 = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_DEMOTRF5_TRF5, 0, length);
				strReturn = new String(commonByteArray_DEMOTRF5_TRF5, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			String strReturn = null;
			int length = 0;
			length = unmarshaller.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_DEMOTRF5_TRF5.length) {
					if (length < 1024 && commonByteArray_DEMOTRF5_TRF5.length == 0) {
						commonByteArray_DEMOTRF5_TRF5 = new byte[1024];
					} else {
						commonByteArray_DEMOTRF5_TRF5 = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_DEMOTRF5_TRF5, 0, length);
				strReturn = new String(commonByteArray_DEMOTRF5_TRF5, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos) throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (str == null) {
				marshaller.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				marshaller.writeInt(byteArray.length);
				marshaller.write(byteArray);
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_DEMOTRF5_TRF5) {

				try {

					int length = 0;

					this.id_assunto = dis.readInt();

					this.cod_assunto_cnj = readString(dis);

					this.descricao_assunto = readString(dis);

					this.ramo_direito = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_DEMOTRF5_TRF5) {

				try {

					int length = 0;

					this.id_assunto = dis.readInt();

					this.cod_assunto_cnj = readString(dis);

					this.descricao_assunto = readString(dis);

					this.ramo_direito = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// int

				dos.writeInt(this.id_assunto);

				// String

				writeString(this.cod_assunto_cnj, dos);

				// String

				writeString(this.descricao_assunto, dos);

				// String

				writeString(this.ramo_direito, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// int

				dos.writeInt(this.id_assunto);

				// String

				writeString(this.cod_assunto_cnj, dos);

				// String

				writeString(this.descricao_assunto, dos);

				// String

				writeString(this.ramo_direito, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("id_assunto=" + String.valueOf(id_assunto));
			sb.append(",cod_assunto_cnj=" + cod_assunto_cnj);
			sb.append(",descricao_assunto=" + descricao_assunto);
			sb.append(",ramo_direito=" + ramo_direito);
			sb.append("]");

			return sb.toString();
		}

		public String toLogString() {
			StringBuilder sb = new StringBuilder();

			sb.append(id_assunto);

			sb.append("|");

			if (cod_assunto_cnj == null) {
				sb.append("<null>");
			} else {
				sb.append(cod_assunto_cnj);
			}

			sb.append("|");

			if (descricao_assunto == null) {
				sb.append("<null>");
			} else {
				sb.append(descricao_assunto);
			}

			sb.append("|");

			if (ramo_direito == null) {
				sb.append("<null>");
			} else {
				sb.append(ramo_direito);
			}

			sb.append("|");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(Processos_Ingestao_Bronze_1_row4Struct other) {

			int returnValue = -1;

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(), object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public void Processos_Ingestao_Bronze_1_tDBInput_4Process(final java.util.Map<String, Object> globalMap)
			throws TalendException {
		globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_4_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("Processos_Ingestao_Bronze_1_tDBInput_4", "jfKRqC_");

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				Processos_Ingestao_Bronze_1_row4Struct Processos_Ingestao_Bronze_1_row4 = new Processos_Ingestao_Bronze_1_row4Struct();

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_4 begin ] start
				 */

				sh("Processos_Ingestao_Bronze_1_tFileOutputParquet_4");

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_4");

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0,
						"Processos_Ingestao_Bronze_1_row4");

				int tos_count_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = 0;

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_4 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = new StringBuilder();
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_4.append("Parameters:");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_4.append("FILENAME" + " = "
									+ "\"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/assunto_bronze\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
									.append("FILE_ACTION" + " = " + "OVERWRITE");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
									.append("COMPRESSION" + " = " + "\"UNCOMPRESSED\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
									.append("USE_EXTERNAL_HADOOP_DEPS" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
									.append("ROW_GROUP_SIZE" + " = " + "134217728");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
									.append("PAGE_SIZE" + " = " + "1048576");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_4.append(" | ");
							if (log.isDebugEnabled())
								log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_4 - "
										+ (log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_4));
						}
					}
					new BytesLimit65535_Processos_Ingestao_Bronze_1_tFileOutputParquet_4().limitLog4jByte();
				}
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter(
								final Processos_Ingestao_Bronze_1_row4Struct Processos_Ingestao_Bronze_1_row4)
								throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("FILENAME", String.valueOf(
										"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/assunto_bronze"));
								component_parameters.put("FILE_ACTION", String.valueOf("OVERWRITE"));
								component_parameters.put("COMPRESSION", String.valueOf("UNCOMPRESSED"));
								component_parameters.put("USE_EXTERNAL_HADOOP_DEPS", String.valueOf("false"));
								component_parameters.put("ROW_GROUP_SIZE", String.valueOf("134217728"));
								component_parameters.put("PAGE_SIZE", String.valueOf("1048576"));

							} catch (java.lang.Exception e_Processos_Ingestao_Bronze_1_tFileOutputParquet_4) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("Processos_Ingestao_Bronze_1_tFileOutputParquet_4",
							"tFileOutputParquet", new ParameterUtil_Processos_Ingestao_Bronze_1_tFileOutputParquet_4()
									.getParameter(Processos_Ingestao_Bronze_1_row4));
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("Processos_Ingestao_Bronze_1_tFileOutputParquet_4", "tFileOutputParquet_4",
							"tFileOutputParquet");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				int nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = 0;
				org.apache.parquet.schema.Types.MessageTypeBuilder schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = org.apache.parquet.schema.Types
						.buildMessage();
				org.apache.parquet.schema.MessageType messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = null;
				org.talend.parquet.data.simple.SimpleGroupFactory factory_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = null;
				org.apache.parquet.hadoop.ParquetWriter<org.talend.parquet.data.Group> writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = null;

				String filePath_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = "C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/assunto_bronze";
				globalMap.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_4_FILE_PATH",
						filePath_Processos_Ingestao_Bronze_1_tFileOutputParquet_4);
				org.apache.hadoop.conf.Configuration config_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = new org.apache.hadoop.conf.Configuration();
				config_Processos_Ingestao_Bronze_1_tFileOutputParquet_4.set("fs.file.impl",
						org.apache.hadoop.fs.LocalFileSystem.class.getName());
				org.apache.hadoop.fs.Path path_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = new org.apache.hadoop.fs.Path(
						filePath_Processos_Ingestao_Bronze_1_tFileOutputParquet_4);
				// CRC file path
				String crcName_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = "."
						+ path_Processos_Ingestao_Bronze_1_tFileOutputParquet_4.getName() + ".crc";
				org.apache.hadoop.fs.Path crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = new org.apache.hadoop.fs.Path(
						path_Processos_Ingestao_Bronze_1_tFileOutputParquet_4.getParent(),
						crcName_Processos_Ingestao_Bronze_1_tFileOutputParquet_4);
				String compressName_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = "UNCOMPRESSED";
				int rowGroupSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = 134217728;
				int pageSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = 1048576;
				java.util.Map<String, org.talend.parquet.data.simple.SimpleGroupFactory> cachedFactory_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = new java.util.HashMap<>();
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("id_assunto", false,
								"INT32", "INT_32"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("cod_assunto_cnj",
								true, "BINARY", "UTF8"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("descricao_assunto",
								true, "BINARY", "UTF8"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("ramo_direito", true,
								"BINARY", "UTF8"));
				messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
						.named("Schema");

				org.talend.parquet.hadoop.TalendGroupWriteSupport.setSchema(
						messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_4,
						config_Processos_Ingestao_Bronze_1_tFileOutputParquet_4);
				factory_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = new org.talend.parquet.data.simple.SimpleGroupFactory(
						messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_4);
				org.apache.parquet.hadoop.ParquetWriter.Builder<org.talend.parquet.data.Group, org.talend.parquet.hadoop.TalendParquetWriter.Builder> builder_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = org.talend.parquet.hadoop.TalendParquetWriter
						.builder(org.apache.parquet.hadoop.util.HadoopOutputFile.fromPath(
								path_Processos_Ingestao_Bronze_1_tFileOutputParquet_4,
								config_Processos_Ingestao_Bronze_1_tFileOutputParquet_4));
				builder_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
						.withWriteMode(org.apache.parquet.hadoop.ParquetFileWriter.Mode.OVERWRITE)
						.withCompressionCodec(org.apache.parquet.hadoop.metadata.CompressionCodecName
								.fromConf(compressName_Processos_Ingestao_Bronze_1_tFileOutputParquet_4))
						.withRowGroupSize(rowGroupSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_4)
						.withPageSize(pageSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_4)
						.withConf(config_Processos_Ingestao_Bronze_1_tFileOutputParquet_4);

				writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = builder_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
						.build();

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_4 begin ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_4 begin ] start
				 */

				sh("Processos_Ingestao_Bronze_1_tDBInput_4");

				s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_4");

				int tos_count_Processos_Ingestao_Bronze_1_tDBInput_4 = 0;

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tDBInput_4 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_Processos_Ingestao_Bronze_1_tDBInput_4 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4 = new StringBuilder();
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append("Parameters:");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4
									.append("USE_EXISTING_CONNECTION" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append("DB_VERSION" + " = " + "V9_X");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append("HOST" + " = "
									+ "\"peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append("PORT" + " = " + "\"5432\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4
									.append("DBNAME" + " = " + "\"qlik_demo\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4
									.append("SCHEMA_DB" + " = " + "\"public\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4
									.append("USER" + " = " + "\"peta_qlik\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append("PASS" + " = "
									+ String.valueOf(
											"enc:routine.encryption.key.v1:E+pISB50rHhQdZpkUgu8DyabLY/VvO4XVFXK6ct7f3EHXYlkZA==")
											.substring(0, 4)
									+ "...");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append("QUERYSTORE" + " = " + "\"\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append("QUERY" + " = "
									+ "\"SELECT    \\\"public\\\".\\\"assunto\\\".\\\"id_assunto\\\",    \\\"public\\\".\\\"assunto\\\".\\\"cod_assunto_cnj\\\",    \\\"public\\\".\\\"assunto\\\".\\\"descricao_assunto\\\",    \\\"public\\\".\\\"assunto\\\".\\\"ramo_direito\\\"   FROM \\\"public\\\".\\\"assunto\\\"\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4
									.append("SPECIFY_DATASOURCE_ALIAS" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4
									.append("PROPERTIES" + " = " + "\"classe\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4
									.append("USE_CURSOR" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4
									.append("TRIM_ALL_COLUMN" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(
									"TRIM_COLUMN" + " = " + "[{TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("id_assunto")
											+ "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("cod_assunto_cnj")
											+ "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("descricao_assunto")
											+ "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("ramo_direito") + "}]");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4
									.append("UNIFIED_COMPONENTS" + " = " + "tPostgresqlInput");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4.append(" | ");
							if (log.isDebugEnabled())
								log.debug("Processos_Ingestao_Bronze_1_tDBInput_4 - "
										+ (log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_4));
						}
					}
					new BytesLimit65535_Processos_Ingestao_Bronze_1_tDBInput_4().limitLog4jByte();
				}
				boolean init_Processos_Ingestao_Bronze_1_tDBInput_4_0 = true;
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_Processos_Ingestao_Bronze_1_tDBInput_4 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter() throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("USE_EXISTING_CONNECTION", String.valueOf("false"));
								component_parameters.put("DB_VERSION", String.valueOf("V9_X"));
								component_parameters.put("HOST", String
										.valueOf("peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com"));
								component_parameters.put("PORT", String.valueOf("5432"));
								component_parameters.put("DBNAME", String.valueOf("qlik_demo"));
								component_parameters.put("SCHEMA_DB", String.valueOf("public"));
								component_parameters.put("USER", String.valueOf("peta_qlik"));
								component_parameters.put("QUERYSTORE", String.valueOf(""));
								component_parameters.put("QUERY", String.valueOf(new StringBuilder().append(
										"SELECT \n  \"public\".\"assunto\".\"id_assunto\", \n  \"public\".\"assunto\".\"cod_assunto_cnj\", \n  \"public\".\"assunto"
												+ "\".\"descricao_assunto\", \n  \"public\".\"assunto\".\"ramo_direito\" \n FROM \"public\".\"assunto\"")
										.toString()));
								component_parameters.put("SPECIFY_DATASOURCE_ALIAS", String.valueOf("false"));
								component_parameters.put("PROPERTIES", String.valueOf("classe"));
								component_parameters.put("USE_CURSOR", String.valueOf("false"));
								component_parameters.put("TRIM_ALL_COLUMN", String.valueOf("false"));
								component_parameters.put("TRIM_COLUMN",
										String.valueOf(new StringBuilder().append("[{TRIM=").append("false")
												.append(", SCHEMA_COLUMN=").append("id_assunto").append("}, {TRIM=")
												.append("false").append(", SCHEMA_COLUMN=").append("cod_assunto_cnj")
												.append("}, {TRIM=").append("false").append(", SCHEMA_COLUMN=")
												.append("descricao_assunto").append("}, {TRIM=").append("false")
												.append(", SCHEMA_COLUMN=").append("ramo_direito").append("}]")
												.toString()));
								component_parameters.put("UNIFIED_COMPONENTS", String.valueOf("tPostgresqlInput"));

							} catch (java.lang.Exception e_Processos_Ingestao_Bronze_1_tDBInput_4) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("Processos_Ingestao_Bronze_1_tDBInput_4",
							"tPostgresqlInput",
							new ParameterUtil_Processos_Ingestao_Bronze_1_tDBInput_4().getParameter());
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("Processos_Ingestao_Bronze_1_tDBInput_4", "tDBInput_4", "tPostgresqlInput");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				int nb_line_Processos_Ingestao_Bronze_1_tDBInput_4 = 0;
				java.sql.Connection conn_Processos_Ingestao_Bronze_1_tDBInput_4 = null;
				String driverClass_Processos_Ingestao_Bronze_1_tDBInput_4 = "org.postgresql.Driver";
				java.lang.Class jdbcclazz_Processos_Ingestao_Bronze_1_tDBInput_4 = java.lang.Class
						.forName(driverClass_Processos_Ingestao_Bronze_1_tDBInput_4);
				String dbUser_Processos_Ingestao_Bronze_1_tDBInput_4 = "peta_qlik";

				final String decryptedPassword_Processos_Ingestao_Bronze_1_tDBInput_4 = java.util.Optional
						.ofNullable(routines.system.PasswordEncryptUtil.decryptPassword(
								"enc:routine.encryption.key.v1:/kzW7yr2+XUUhJDDi4ogFW1wovztyIorsyxfwjUEDM2zBaR/wQ=="))
						.orElse("");

				String dbPwd_Processos_Ingestao_Bronze_1_tDBInput_4 = decryptedPassword_Processos_Ingestao_Bronze_1_tDBInput_4;

				String url_Processos_Ingestao_Bronze_1_tDBInput_4 = "jdbc:postgresql://"
						+ "peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com" + ":" + "5432" + "/"
						+ "qlik_demo" + "?" + "classe";

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_4 - Driver ClassName: "
						+ driverClass_Processos_Ingestao_Bronze_1_tDBInput_4 + ".");

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_4 - Connection attempt to '"
						+ url_Processos_Ingestao_Bronze_1_tDBInput_4.replaceAll("(?<=trustStorePassword=)[^;]*",
								"********")
						+ "' with the username '" + dbUser_Processos_Ingestao_Bronze_1_tDBInput_4 + "'.");

				conn_Processos_Ingestao_Bronze_1_tDBInput_4 = java.sql.DriverManager.getConnection(
						url_Processos_Ingestao_Bronze_1_tDBInput_4, dbUser_Processos_Ingestao_Bronze_1_tDBInput_4,
						dbPwd_Processos_Ingestao_Bronze_1_tDBInput_4);
				log.debug("Processos_Ingestao_Bronze_1_tDBInput_4 - Connection to '"
						+ url_Processos_Ingestao_Bronze_1_tDBInput_4.replaceAll("(?<=trustStorePassword=)[^;]*",
								"********")
						+ "' has succeeded.");

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_4 - Connection is set auto commit to 'false'.");

				conn_Processos_Ingestao_Bronze_1_tDBInput_4.setAutoCommit(false);

				java.sql.Statement stmt_Processos_Ingestao_Bronze_1_tDBInput_4 = conn_Processos_Ingestao_Bronze_1_tDBInput_4
						.createStatement();

				String dbquery_Processos_Ingestao_Bronze_1_tDBInput_4 = new StringBuilder().append(
						"SELECT \n  \"public\".\"assunto\".\"id_assunto\", \n  \"public\".\"assunto\".\"cod_assunto_cnj\", \n  \"public\".\"assunto"
								+ "\".\"descricao_assunto\", \n  \"public\".\"assunto\".\"ramo_direito\" \n FROM \"public\".\"assunto\"")
						.toString();

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_4 - Executing the query: '"
						+ dbquery_Processos_Ingestao_Bronze_1_tDBInput_4 + "'.");

				globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_4_QUERY",
						dbquery_Processos_Ingestao_Bronze_1_tDBInput_4);

				java.sql.ResultSet rs_Processos_Ingestao_Bronze_1_tDBInput_4 = null;

				try {
					rs_Processos_Ingestao_Bronze_1_tDBInput_4 = stmt_Processos_Ingestao_Bronze_1_tDBInput_4
							.executeQuery(dbquery_Processos_Ingestao_Bronze_1_tDBInput_4);
					java.sql.ResultSetMetaData rsmd_Processos_Ingestao_Bronze_1_tDBInput_4 = rs_Processos_Ingestao_Bronze_1_tDBInput_4
							.getMetaData();
					int colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_4 = rsmd_Processos_Ingestao_Bronze_1_tDBInput_4
							.getColumnCount();

					String tmpContent_Processos_Ingestao_Bronze_1_tDBInput_4 = null;

					log.debug("Processos_Ingestao_Bronze_1_tDBInput_4 - Retrieving records from the database.");

					while (rs_Processos_Ingestao_Bronze_1_tDBInput_4.next()) {
						nb_line_Processos_Ingestao_Bronze_1_tDBInput_4++;

						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_4 < 1) {
							Processos_Ingestao_Bronze_1_row4.id_assunto = 0;
						} else {

							Processos_Ingestao_Bronze_1_row4.id_assunto = rs_Processos_Ingestao_Bronze_1_tDBInput_4
									.getInt(1);
							if (rs_Processos_Ingestao_Bronze_1_tDBInput_4.wasNull()) {
								throw new RuntimeException("Null value in non-Nullable column");
							}
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_4 < 2) {
							Processos_Ingestao_Bronze_1_row4.cod_assunto_cnj = null;
						} else {

							Processos_Ingestao_Bronze_1_row4.cod_assunto_cnj = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_4, 2, false);
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_4 < 3) {
							Processos_Ingestao_Bronze_1_row4.descricao_assunto = null;
						} else {

							Processos_Ingestao_Bronze_1_row4.descricao_assunto = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_4, 3, false);
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_4 < 4) {
							Processos_Ingestao_Bronze_1_row4.ramo_direito = null;
						} else {

							Processos_Ingestao_Bronze_1_row4.ramo_direito = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_4, 4, false);
						}

						log.debug("Processos_Ingestao_Bronze_1_tDBInput_4 - Retrieving the record "
								+ nb_line_Processos_Ingestao_Bronze_1_tDBInput_4 + ".");

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_4 begin ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_4 main ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_4");

						// QTUP-3575
						if (enableLineage && init_Processos_Ingestao_Bronze_1_tDBInput_4_0) {
							class SchemaUtil_Processos_Ingestao_Bronze_1_row4 {

								private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
									java.util.Map<String, String> field = new java.util.HashMap<>();
									field.put("name", values[0]);
									field.put("origin_name", values[1]);
									field.put("iskey", values[2]);
									field.put("talend_type", values[3]);
									field.put("type", values[4]);
									field.put("nullable", values[5]);
									field.put("pattern", values[6]);
									field.put("length", values[7]);
									field.put("precision", values[8]);
									schema.add(field);
								}

								public java.util.List<java.util.Map<String, String>> getSchema(
										final Processos_Ingestao_Bronze_1_row4Struct Processos_Ingestao_Bronze_1_row4) {
									java.util.List<java.util.Map<String, String>> s = new java.util.ArrayList<>();
									if (Processos_Ingestao_Bronze_1_row4 == null) {
										return s;
									}
									a(s, "id_assunto", "id_assunto", "false", "id_Integer", "SERIAL", "false", "", "10",
											"0");
									a(s, "cod_assunto_cnj", "cod_assunto_cnj", "false", "id_String", "VARCHAR", "true",
											"", "20", "0");
									a(s, "descricao_assunto", "descricao_assunto", "false", "id_String", "TEXT", "true",
											"", "2147483647", "0");
									a(s, "ramo_direito", "ramo_direito", "false", "id_String", "VARCHAR", "true", "",
											"100", "0");
									return s;
								}

							}

							if (Processos_Ingestao_Bronze_1_row4 != null) {
								talendJobLog.addConnectionSchemaMessage("Processos_Ingestao_Bronze_1_tDBInput_4",
										"tPostgresqlInput", "Processos_Ingestao_Bronze_1_tFileOutputParquet_4",
										"tFileOutputParquet", "Processos_Ingestao_Bronze_1_row4" + iterateId,
										new SchemaUtil_Processos_Ingestao_Bronze_1_row4()
												.getSchema(Processos_Ingestao_Bronze_1_row4));
								talendJobLogProcess(globalMap);
								init_Processos_Ingestao_Bronze_1_tDBInput_4_0 = false;
							}

						}
						// QTUP-3575

						tos_count_Processos_Ingestao_Bronze_1_tDBInput_4++;

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_4 main ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_4 process_data_begin ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_4");

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_4 process_data_begin ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_4 main ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_4");

						if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

								, "Processos_Ingestao_Bronze_1_row4", "Processos_Ingestao_Bronze_1_tDBInput_4",
								"tDBInput_4", "tPostgresqlInput", "Processos_Ingestao_Bronze_1_tFileOutputParquet_4",
								"tFileOutputParquet_4", "tFileOutputParquet"

						)) {
							talendJobLogProcess(globalMap);
						}

						if (log.isTraceEnabled()) {
							log.trace("Processos_Ingestao_Bronze_1_row4 - "
									+ (Processos_Ingestao_Bronze_1_row4 == null ? ""
											: Processos_Ingestao_Bronze_1_row4.toLogString()));
						}

						org.talend.parquet.data.Group group_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = factory_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
								.newGroup();

						group_Processos_Ingestao_Bronze_1_tFileOutputParquet_4.append("id_assunto",
								Processos_Ingestao_Bronze_1_row4.id_assunto);
						if (Processos_Ingestao_Bronze_1_row4.cod_assunto_cnj != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_4.append("cod_assunto_cnj",
									String.valueOf(Processos_Ingestao_Bronze_1_row4.cod_assunto_cnj));
						}

						if (Processos_Ingestao_Bronze_1_row4.descricao_assunto != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_4.append("descricao_assunto",
									String.valueOf(Processos_Ingestao_Bronze_1_row4.descricao_assunto));
						}

						if (Processos_Ingestao_Bronze_1_row4.ramo_direito != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_4.append("ramo_direito",
									String.valueOf(Processos_Ingestao_Bronze_1_row4.ramo_direito));
						}

						writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
								.write(group_Processos_Ingestao_Bronze_1_tFileOutputParquet_4);
						nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_4++;
						log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_4 - Writing the record "
								+ nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 + " to the file.");

						tos_count_Processos_Ingestao_Bronze_1_tFileOutputParquet_4++;

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_4 main ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_4 process_data_begin ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_4");

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_4 process_data_begin ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_4 process_data_end ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_4");

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_4 process_data_end ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_4 process_data_end ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_4");

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_4 process_data_end ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_4 end ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_4");

					}
				} finally {
					if (rs_Processos_Ingestao_Bronze_1_tDBInput_4 != null) {
						rs_Processos_Ingestao_Bronze_1_tDBInput_4.close();
					}
					if (stmt_Processos_Ingestao_Bronze_1_tDBInput_4 != null) {
						stmt_Processos_Ingestao_Bronze_1_tDBInput_4.close();
					}
					if (conn_Processos_Ingestao_Bronze_1_tDBInput_4 != null
							&& !conn_Processos_Ingestao_Bronze_1_tDBInput_4.isClosed()) {

						log.debug("Processos_Ingestao_Bronze_1_tDBInput_4 - Closing the connection to the database.");

						conn_Processos_Ingestao_Bronze_1_tDBInput_4.close();

						if ("com.mysql.cj.jdbc.Driver".equals((String) globalMap.get("driverClass_"))
								&& routines.system.BundleUtils.inOSGi()) {
							Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread")
									.getMethod("checkedShutdown").invoke(null, (Object[]) null);
						}

						log.debug("Processos_Ingestao_Bronze_1_tDBInput_4 - Connection to the database closed.");

					}

				}
				globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_4_NB_LINE",
						nb_line_Processos_Ingestao_Bronze_1_tDBInput_4);
				log.debug("Processos_Ingestao_Bronze_1_tDBInput_4 - Retrieved records count: "
						+ nb_line_Processos_Ingestao_Bronze_1_tDBInput_4 + " .");

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tDBInput_4 - " + ("Done."));

				ok_Hash.put("Processos_Ingestao_Bronze_1_tDBInput_4", true);
				end_Hash.put("Processos_Ingestao_Bronze_1_tDBInput_4", System.currentTimeMillis());

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_4 end ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_4 end ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_4");

				globalMap.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_4_NB_LINE",
						nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_4);

				log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_4 - Written records count: "
						+ nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 + " .");

				if (writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 != null) {
					writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_4.close();
				}
				org.apache.hadoop.fs.FileSystem fs_Processos_Ingestao_Bronze_1_tFileOutputParquet_4 = crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
						.getFileSystem(config_Processos_Ingestao_Bronze_1_tFileOutputParquet_4);
				if (fs_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
						.exists(crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_4)) {
					fs_Processos_Ingestao_Bronze_1_tFileOutputParquet_4
							.delete(crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_4, false);
				}

				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId,
						"Processos_Ingestao_Bronze_1_row4", 2, 0, "Processos_Ingestao_Bronze_1_tDBInput_4",
						"tDBInput_4", "tPostgresqlInput", "Processos_Ingestao_Bronze_1_tFileOutputParquet_4",
						"tFileOutputParquet_4", "tFileOutputParquet", "output")) {
					talendJobLogProcess(globalMap);
				}

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_4 - " + ("Done."));

				ok_Hash.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_4", true);
				end_Hash.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_4", System.currentTimeMillis());

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_4 end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException) && !(e instanceof TDieException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_4 finally ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_4");

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_4 finally ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_4 finally ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_4");

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_4 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_4_SUBPROCESS_STATE", 1);
	}

	public static class Processos_Ingestao_Bronze_1_row5Struct
			implements routines.system.IPersistableRow<Processos_Ingestao_Bronze_1_row5Struct> {
		final static byte[] commonByteArrayLock_DEMOTRF5_TRF5 = new byte[0];
		static byte[] commonByteArray_DEMOTRF5_TRF5 = new byte[0];

		public int id_magistrado;

		public int getId_magistrado() {
			return this.id_magistrado;
		}

		public Boolean id_magistradoIsNullable() {
			return false;
		}

		public Boolean id_magistradoIsKey() {
			return false;
		}

		public Integer id_magistradoLength() {
			return 10;
		}

		public Integer id_magistradoPrecision() {
			return 0;
		}

		public String id_magistradoDefault() {

			return "";

		}

		public String id_magistradoComment() {

			return "";

		}

		public String id_magistradoPattern() {

			return "";

		}

		public String id_magistradoOriginalDbColumnName() {

			return "id_magistrado";

		}

		public String nome_completo;

		public String getNome_completo() {
			return this.nome_completo;
		}

		public Boolean nome_completoIsNullable() {
			return true;
		}

		public Boolean nome_completoIsKey() {
			return false;
		}

		public Integer nome_completoLength() {
			return 255;
		}

		public Integer nome_completoPrecision() {
			return 0;
		}

		public String nome_completoDefault() {

			return null;

		}

		public String nome_completoComment() {

			return "";

		}

		public String nome_completoPattern() {

			return "";

		}

		public String nome_completoOriginalDbColumnName() {

			return "nome_completo";

		}

		public String cargo;

		public String getCargo() {
			return this.cargo;
		}

		public Boolean cargoIsNullable() {
			return true;
		}

		public Boolean cargoIsKey() {
			return false;
		}

		public Integer cargoLength() {
			return 100;
		}

		public Integer cargoPrecision() {
			return 0;
		}

		public String cargoDefault() {

			return null;

		}

		public String cargoComment() {

			return "";

		}

		public String cargoPattern() {

			return "";

		}

		public String cargoOriginalDbColumnName() {

			return "cargo";

		}

		public String data_posse;

		public String getData_posse() {
			return this.data_posse;
		}

		public Boolean data_posseIsNullable() {
			return true;
		}

		public Boolean data_posseIsKey() {
			return false;
		}

		public Integer data_posseLength() {
			return 50;
		}

		public Integer data_possePrecision() {
			return 0;
		}

		public String data_posseDefault() {

			return null;

		}

		public String data_posseComment() {

			return "";

		}

		public String data_possePattern() {

			return "";

		}

		public String data_posseOriginalDbColumnName() {

			return "data_posse";

		}

		public String situacao;

		public String getSituacao() {
			return this.situacao;
		}

		public Boolean situacaoIsNullable() {
			return true;
		}

		public Boolean situacaoIsKey() {
			return false;
		}

		public Integer situacaoLength() {
			return 20;
		}

		public Integer situacaoPrecision() {
			return 0;
		}

		public String situacaoDefault() {

			return null;

		}

		public String situacaoComment() {

			return "";

		}

		public String situacaoPattern() {

			return "";

		}

		public String situacaoOriginalDbColumnName() {

			return "situacao";

		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_DEMOTRF5_TRF5.length) {
					if (length < 1024 && commonByteArray_DEMOTRF5_TRF5.length == 0) {
						commonByteArray_DEMOTRF5_TRF5 = new byte[1024];
					} else {
						commonByteArray_DEMOTRF5_TRF5 = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_DEMOTRF5_TRF5, 0, length);
				strReturn = new String(commonByteArray_DEMOTRF5_TRF5, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			String strReturn = null;
			int length = 0;
			length = unmarshaller.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_DEMOTRF5_TRF5.length) {
					if (length < 1024 && commonByteArray_DEMOTRF5_TRF5.length == 0) {
						commonByteArray_DEMOTRF5_TRF5 = new byte[1024];
					} else {
						commonByteArray_DEMOTRF5_TRF5 = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_DEMOTRF5_TRF5, 0, length);
				strReturn = new String(commonByteArray_DEMOTRF5_TRF5, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos) throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (str == null) {
				marshaller.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				marshaller.writeInt(byteArray.length);
				marshaller.write(byteArray);
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_DEMOTRF5_TRF5) {

				try {

					int length = 0;

					this.id_magistrado = dis.readInt();

					this.nome_completo = readString(dis);

					this.cargo = readString(dis);

					this.data_posse = readString(dis);

					this.situacao = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_DEMOTRF5_TRF5) {

				try {

					int length = 0;

					this.id_magistrado = dis.readInt();

					this.nome_completo = readString(dis);

					this.cargo = readString(dis);

					this.data_posse = readString(dis);

					this.situacao = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// int

				dos.writeInt(this.id_magistrado);

				// String

				writeString(this.nome_completo, dos);

				// String

				writeString(this.cargo, dos);

				// String

				writeString(this.data_posse, dos);

				// String

				writeString(this.situacao, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// int

				dos.writeInt(this.id_magistrado);

				// String

				writeString(this.nome_completo, dos);

				// String

				writeString(this.cargo, dos);

				// String

				writeString(this.data_posse, dos);

				// String

				writeString(this.situacao, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("id_magistrado=" + String.valueOf(id_magistrado));
			sb.append(",nome_completo=" + nome_completo);
			sb.append(",cargo=" + cargo);
			sb.append(",data_posse=" + data_posse);
			sb.append(",situacao=" + situacao);
			sb.append("]");

			return sb.toString();
		}

		public String toLogString() {
			StringBuilder sb = new StringBuilder();

			sb.append(id_magistrado);

			sb.append("|");

			if (nome_completo == null) {
				sb.append("<null>");
			} else {
				sb.append(nome_completo);
			}

			sb.append("|");

			if (cargo == null) {
				sb.append("<null>");
			} else {
				sb.append(cargo);
			}

			sb.append("|");

			if (data_posse == null) {
				sb.append("<null>");
			} else {
				sb.append(data_posse);
			}

			sb.append("|");

			if (situacao == null) {
				sb.append("<null>");
			} else {
				sb.append(situacao);
			}

			sb.append("|");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(Processos_Ingestao_Bronze_1_row5Struct other) {

			int returnValue = -1;

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(), object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public void Processos_Ingestao_Bronze_1_tDBInput_5Process(final java.util.Map<String, Object> globalMap)
			throws TalendException {
		globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_5_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("Processos_Ingestao_Bronze_1_tDBInput_5", "UTYi2L_");

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				Processos_Ingestao_Bronze_1_row5Struct Processos_Ingestao_Bronze_1_row5 = new Processos_Ingestao_Bronze_1_row5Struct();

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_5 begin ] start
				 */

				sh("Processos_Ingestao_Bronze_1_tFileOutputParquet_5");

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_5");

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0,
						"Processos_Ingestao_Bronze_1_row5");

				int tos_count_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = 0;

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_5 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = new StringBuilder();
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.append("Parameters:");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.append("FILENAME" + " = "
									+ "\"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/magistrado_bronze\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
									.append("FILE_ACTION" + " = " + "OVERWRITE");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
									.append("COMPRESSION" + " = " + "\"UNCOMPRESSED\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
									.append("USE_EXTERNAL_HADOOP_DEPS" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
									.append("ROW_GROUP_SIZE" + " = " + "134217728");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
									.append("PAGE_SIZE" + " = " + "1048576");
							log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.append(" | ");
							if (log.isDebugEnabled())
								log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_5 - "
										+ (log4jParamters_Processos_Ingestao_Bronze_1_tFileOutputParquet_5));
						}
					}
					new BytesLimit65535_Processos_Ingestao_Bronze_1_tFileOutputParquet_5().limitLog4jByte();
				}
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter(
								final Processos_Ingestao_Bronze_1_row5Struct Processos_Ingestao_Bronze_1_row5)
								throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("FILENAME", String.valueOf(
										"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/magistrado_bronze"));
								component_parameters.put("FILE_ACTION", String.valueOf("OVERWRITE"));
								component_parameters.put("COMPRESSION", String.valueOf("UNCOMPRESSED"));
								component_parameters.put("USE_EXTERNAL_HADOOP_DEPS", String.valueOf("false"));
								component_parameters.put("ROW_GROUP_SIZE", String.valueOf("134217728"));
								component_parameters.put("PAGE_SIZE", String.valueOf("1048576"));

							} catch (java.lang.Exception e_Processos_Ingestao_Bronze_1_tFileOutputParquet_5) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("Processos_Ingestao_Bronze_1_tFileOutputParquet_5",
							"tFileOutputParquet", new ParameterUtil_Processos_Ingestao_Bronze_1_tFileOutputParquet_5()
									.getParameter(Processos_Ingestao_Bronze_1_row5));
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("Processos_Ingestao_Bronze_1_tFileOutputParquet_5", "tFileOutputParquet_5",
							"tFileOutputParquet");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				int nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = 0;
				org.apache.parquet.schema.Types.MessageTypeBuilder schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = org.apache.parquet.schema.Types
						.buildMessage();
				org.apache.parquet.schema.MessageType messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = null;
				org.talend.parquet.data.simple.SimpleGroupFactory factory_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = null;
				org.apache.parquet.hadoop.ParquetWriter<org.talend.parquet.data.Group> writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = null;

				String filePath_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = "C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/magistrado_bronze";
				globalMap.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_5_FILE_PATH",
						filePath_Processos_Ingestao_Bronze_1_tFileOutputParquet_5);
				org.apache.hadoop.conf.Configuration config_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = new org.apache.hadoop.conf.Configuration();
				config_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.set("fs.file.impl",
						org.apache.hadoop.fs.LocalFileSystem.class.getName());
				org.apache.hadoop.fs.Path path_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = new org.apache.hadoop.fs.Path(
						filePath_Processos_Ingestao_Bronze_1_tFileOutputParquet_5);
				// CRC file path
				String crcName_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = "."
						+ path_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.getName() + ".crc";
				org.apache.hadoop.fs.Path crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = new org.apache.hadoop.fs.Path(
						path_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.getParent(),
						crcName_Processos_Ingestao_Bronze_1_tFileOutputParquet_5);
				String compressName_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = "UNCOMPRESSED";
				int rowGroupSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = 134217728;
				int pageSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = 1048576;
				java.util.Map<String, org.talend.parquet.data.simple.SimpleGroupFactory> cachedFactory_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = new java.util.HashMap<>();
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("id_magistrado",
								false, "INT32", "INT_32"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("nome_completo", true,
								"BINARY", "UTF8"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("cargo", true,
								"BINARY", "UTF8"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("data_posse", true,
								"BINARY", "UTF8"));
				schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
						.addField(org.talend.parquet.utils.TalendParquetUtils.createPrimitiveType("situacao", true,
								"BINARY", "UTF8"));
				messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = schemaBuilder_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
						.named("Schema");

				org.talend.parquet.hadoop.TalendGroupWriteSupport.setSchema(
						messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_5,
						config_Processos_Ingestao_Bronze_1_tFileOutputParquet_5);
				factory_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = new org.talend.parquet.data.simple.SimpleGroupFactory(
						messageType_Processos_Ingestao_Bronze_1_tFileOutputParquet_5);
				org.apache.parquet.hadoop.ParquetWriter.Builder<org.talend.parquet.data.Group, org.talend.parquet.hadoop.TalendParquetWriter.Builder> builder_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = org.talend.parquet.hadoop.TalendParquetWriter
						.builder(org.apache.parquet.hadoop.util.HadoopOutputFile.fromPath(
								path_Processos_Ingestao_Bronze_1_tFileOutputParquet_5,
								config_Processos_Ingestao_Bronze_1_tFileOutputParquet_5));
				builder_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
						.withWriteMode(org.apache.parquet.hadoop.ParquetFileWriter.Mode.OVERWRITE)
						.withCompressionCodec(org.apache.parquet.hadoop.metadata.CompressionCodecName
								.fromConf(compressName_Processos_Ingestao_Bronze_1_tFileOutputParquet_5))
						.withRowGroupSize(rowGroupSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_5)
						.withPageSize(pageSize_Processos_Ingestao_Bronze_1_tFileOutputParquet_5)
						.withConf(config_Processos_Ingestao_Bronze_1_tFileOutputParquet_5);

				writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = builder_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
						.build();

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_5 begin ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_5 begin ] start
				 */

				sh("Processos_Ingestao_Bronze_1_tDBInput_5");

				s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_5");

				int tos_count_Processos_Ingestao_Bronze_1_tDBInput_5 = 0;

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tDBInput_5 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_Processos_Ingestao_Bronze_1_tDBInput_5 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5 = new StringBuilder();
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append("Parameters:");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5
									.append("USE_EXISTING_CONNECTION" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append("DB_VERSION" + " = " + "V9_X");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append("HOST" + " = "
									+ "\"peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append("PORT" + " = " + "\"5432\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5
									.append("DBNAME" + " = " + "\"qlik_demo\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5
									.append("SCHEMA_DB" + " = " + "\"public\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5
									.append("USER" + " = " + "\"peta_qlik\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append("PASS" + " = "
									+ String.valueOf(
											"enc:routine.encryption.key.v1:ECCp8wzxC5hRz1lwHiGeTbDMIT/bk8kUDUtvvYp99rCr96o0mg==")
											.substring(0, 4)
									+ "...");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append("QUERYSTORE" + " = " + "\"\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append("QUERY" + " = "
									+ "\"SELECT    \\\"public\\\".\\\"magistrado\\\".\\\"id_magistrado\\\",    \\\"public\\\".\\\"magistrado\\\".\\\"nome_completo\\\",    \\\"public\\\".\\\"magistrado\\\".\\\"cargo\\\",    \\\"public\\\".\\\"magistrado\\\".\\\"data_posse\\\",    \\\"public\\\".\\\"magistrado\\\".\\\"situacao\\\"  FROM \\\"public\\\".\\\"magistrado\\\"\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5
									.append("SPECIFY_DATASOURCE_ALIAS" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5
									.append("PROPERTIES" + " = " + "\"classe\"");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5
									.append("USE_CURSOR" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5
									.append("TRIM_ALL_COLUMN" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5
									.append("TRIM_COLUMN" + " = " + "[{TRIM=" + ("false") + ", SCHEMA_COLUMN="
											+ ("id_magistrado") + "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN="
											+ ("nome_completo") + "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN="
											+ ("cargo") + "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("data_posse")
											+ "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("situacao") + "}]");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5
									.append("UNIFIED_COMPONENTS" + " = " + "tPostgresqlInput");
							log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5.append(" | ");
							if (log.isDebugEnabled())
								log.debug("Processos_Ingestao_Bronze_1_tDBInput_5 - "
										+ (log4jParamters_Processos_Ingestao_Bronze_1_tDBInput_5));
						}
					}
					new BytesLimit65535_Processos_Ingestao_Bronze_1_tDBInput_5().limitLog4jByte();
				}
				boolean init_Processos_Ingestao_Bronze_1_tDBInput_5_0 = true;
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_Processos_Ingestao_Bronze_1_tDBInput_5 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter() throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("USE_EXISTING_CONNECTION", String.valueOf("false"));
								component_parameters.put("DB_VERSION", String.valueOf("V9_X"));
								component_parameters.put("HOST", String
										.valueOf("peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com"));
								component_parameters.put("PORT", String.valueOf("5432"));
								component_parameters.put("DBNAME", String.valueOf("qlik_demo"));
								component_parameters.put("SCHEMA_DB", String.valueOf("public"));
								component_parameters.put("USER", String.valueOf("peta_qlik"));
								component_parameters.put("QUERYSTORE", String.valueOf(""));
								component_parameters.put("QUERY", String.valueOf(new StringBuilder().append(
										"SELECT \n  \"public\".\"magistrado\".\"id_magistrado\", \n  \"public\".\"magistrado\".\"nome_completo\", \n  \"public\".\""
												+ "magistrado\".\"cargo\", \n  \"public\".\"magistrado\".\"data_posse\", \n  \"public\".\"magistrado\".\"situacao\"\n FROM \"p"
												+ "ublic\".\"magistrado\"")
										.toString()));
								component_parameters.put("SPECIFY_DATASOURCE_ALIAS", String.valueOf("false"));
								component_parameters.put("PROPERTIES", String.valueOf("classe"));
								component_parameters.put("USE_CURSOR", String.valueOf("false"));
								component_parameters.put("TRIM_ALL_COLUMN", String.valueOf("false"));
								component_parameters.put("TRIM_COLUMN",
										String.valueOf(new StringBuilder().append("[{TRIM=").append("false")
												.append(", SCHEMA_COLUMN=").append("id_magistrado").append("}, {TRIM=")
												.append("false").append(", SCHEMA_COLUMN=").append("nome_completo")
												.append("}, {TRIM=").append("false").append(", SCHEMA_COLUMN=")
												.append("cargo").append("}, {TRIM=").append("false")
												.append(", SCHEMA_COLUMN=").append("data_posse").append("}, {TRIM=")
												.append("false").append(", SCHEMA_COLUMN=").append("situacao")
												.append("}]").toString()));
								component_parameters.put("UNIFIED_COMPONENTS", String.valueOf("tPostgresqlInput"));

							} catch (java.lang.Exception e_Processos_Ingestao_Bronze_1_tDBInput_5) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("Processos_Ingestao_Bronze_1_tDBInput_5",
							"tPostgresqlInput",
							new ParameterUtil_Processos_Ingestao_Bronze_1_tDBInput_5().getParameter());
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("Processos_Ingestao_Bronze_1_tDBInput_5", "tDBInput_5", "tPostgresqlInput");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				int nb_line_Processos_Ingestao_Bronze_1_tDBInput_5 = 0;
				java.sql.Connection conn_Processos_Ingestao_Bronze_1_tDBInput_5 = null;
				String driverClass_Processos_Ingestao_Bronze_1_tDBInput_5 = "org.postgresql.Driver";
				java.lang.Class jdbcclazz_Processos_Ingestao_Bronze_1_tDBInput_5 = java.lang.Class
						.forName(driverClass_Processos_Ingestao_Bronze_1_tDBInput_5);
				String dbUser_Processos_Ingestao_Bronze_1_tDBInput_5 = "peta_qlik";

				final String decryptedPassword_Processos_Ingestao_Bronze_1_tDBInput_5 = java.util.Optional
						.ofNullable(routines.system.PasswordEncryptUtil.decryptPassword(
								"enc:routine.encryption.key.v1:9e8PgGJIXiL8ofuYjkLZjZkMGLJ+i1KYKv0wEKinKi5aLVNOAA=="))
						.orElse("");

				String dbPwd_Processos_Ingestao_Bronze_1_tDBInput_5 = decryptedPassword_Processos_Ingestao_Bronze_1_tDBInput_5;

				String url_Processos_Ingestao_Bronze_1_tDBInput_5 = "jdbc:postgresql://"
						+ "peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com" + ":" + "5432" + "/"
						+ "qlik_demo" + "?" + "classe";

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_5 - Driver ClassName: "
						+ driverClass_Processos_Ingestao_Bronze_1_tDBInput_5 + ".");

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_5 - Connection attempt to '"
						+ url_Processos_Ingestao_Bronze_1_tDBInput_5.replaceAll("(?<=trustStorePassword=)[^;]*",
								"********")
						+ "' with the username '" + dbUser_Processos_Ingestao_Bronze_1_tDBInput_5 + "'.");

				conn_Processos_Ingestao_Bronze_1_tDBInput_5 = java.sql.DriverManager.getConnection(
						url_Processos_Ingestao_Bronze_1_tDBInput_5, dbUser_Processos_Ingestao_Bronze_1_tDBInput_5,
						dbPwd_Processos_Ingestao_Bronze_1_tDBInput_5);
				log.debug("Processos_Ingestao_Bronze_1_tDBInput_5 - Connection to '"
						+ url_Processos_Ingestao_Bronze_1_tDBInput_5.replaceAll("(?<=trustStorePassword=)[^;]*",
								"********")
						+ "' has succeeded.");

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_5 - Connection is set auto commit to 'false'.");

				conn_Processos_Ingestao_Bronze_1_tDBInput_5.setAutoCommit(false);

				java.sql.Statement stmt_Processos_Ingestao_Bronze_1_tDBInput_5 = conn_Processos_Ingestao_Bronze_1_tDBInput_5
						.createStatement();

				String dbquery_Processos_Ingestao_Bronze_1_tDBInput_5 = new StringBuilder().append(
						"SELECT \n  \"public\".\"magistrado\".\"id_magistrado\", \n  \"public\".\"magistrado\".\"nome_completo\", \n  \"public\".\""
								+ "magistrado\".\"cargo\", \n  \"public\".\"magistrado\".\"data_posse\", \n  \"public\".\"magistrado\".\"situacao\"\n FROM \"p"
								+ "ublic\".\"magistrado\"")
						.toString();

				log.debug("Processos_Ingestao_Bronze_1_tDBInput_5 - Executing the query: '"
						+ dbquery_Processos_Ingestao_Bronze_1_tDBInput_5 + "'.");

				globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_5_QUERY",
						dbquery_Processos_Ingestao_Bronze_1_tDBInput_5);

				java.sql.ResultSet rs_Processos_Ingestao_Bronze_1_tDBInput_5 = null;

				try {
					rs_Processos_Ingestao_Bronze_1_tDBInput_5 = stmt_Processos_Ingestao_Bronze_1_tDBInput_5
							.executeQuery(dbquery_Processos_Ingestao_Bronze_1_tDBInput_5);
					java.sql.ResultSetMetaData rsmd_Processos_Ingestao_Bronze_1_tDBInput_5 = rs_Processos_Ingestao_Bronze_1_tDBInput_5
							.getMetaData();
					int colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_5 = rsmd_Processos_Ingestao_Bronze_1_tDBInput_5
							.getColumnCount();

					String tmpContent_Processos_Ingestao_Bronze_1_tDBInput_5 = null;

					log.debug("Processos_Ingestao_Bronze_1_tDBInput_5 - Retrieving records from the database.");

					while (rs_Processos_Ingestao_Bronze_1_tDBInput_5.next()) {
						nb_line_Processos_Ingestao_Bronze_1_tDBInput_5++;

						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_5 < 1) {
							Processos_Ingestao_Bronze_1_row5.id_magistrado = 0;
						} else {

							Processos_Ingestao_Bronze_1_row5.id_magistrado = rs_Processos_Ingestao_Bronze_1_tDBInput_5
									.getInt(1);
							if (rs_Processos_Ingestao_Bronze_1_tDBInput_5.wasNull()) {
								throw new RuntimeException("Null value in non-Nullable column");
							}
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_5 < 2) {
							Processos_Ingestao_Bronze_1_row5.nome_completo = null;
						} else {

							Processos_Ingestao_Bronze_1_row5.nome_completo = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_5, 2, false);
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_5 < 3) {
							Processos_Ingestao_Bronze_1_row5.cargo = null;
						} else {

							Processos_Ingestao_Bronze_1_row5.cargo = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_5, 3, false);
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_5 < 4) {
							Processos_Ingestao_Bronze_1_row5.data_posse = null;
						} else {

							Processos_Ingestao_Bronze_1_row5.data_posse = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_5, 4, false);
						}
						if (colQtyInRs_Processos_Ingestao_Bronze_1_tDBInput_5 < 5) {
							Processos_Ingestao_Bronze_1_row5.situacao = null;
						} else {

							Processos_Ingestao_Bronze_1_row5.situacao = routines.system.JDBCUtil
									.getString(rs_Processos_Ingestao_Bronze_1_tDBInput_5, 5, false);
						}

						log.debug("Processos_Ingestao_Bronze_1_tDBInput_5 - Retrieving the record "
								+ nb_line_Processos_Ingestao_Bronze_1_tDBInput_5 + ".");

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_5 begin ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_5 main ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_5");

						// QTUP-3575
						if (enableLineage && init_Processos_Ingestao_Bronze_1_tDBInput_5_0) {
							class SchemaUtil_Processos_Ingestao_Bronze_1_row5 {

								private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
									java.util.Map<String, String> field = new java.util.HashMap<>();
									field.put("name", values[0]);
									field.put("origin_name", values[1]);
									field.put("iskey", values[2]);
									field.put("talend_type", values[3]);
									field.put("type", values[4]);
									field.put("nullable", values[5]);
									field.put("pattern", values[6]);
									field.put("length", values[7]);
									field.put("precision", values[8]);
									schema.add(field);
								}

								public java.util.List<java.util.Map<String, String>> getSchema(
										final Processos_Ingestao_Bronze_1_row5Struct Processos_Ingestao_Bronze_1_row5) {
									java.util.List<java.util.Map<String, String>> s = new java.util.ArrayList<>();
									if (Processos_Ingestao_Bronze_1_row5 == null) {
										return s;
									}
									a(s, "id_magistrado", "id_magistrado", "false", "id_Integer", "SERIAL", "false", "",
											"10", "0");
									a(s, "nome_completo", "nome_completo", "false", "id_String", "VARCHAR", "true", "",
											"255", "0");
									a(s, "cargo", "cargo", "false", "id_String", "VARCHAR", "true", "", "100", "0");
									a(s, "data_posse", "data_posse", "false", "id_String", "VARCHAR", "true", "", "50",
											"0");
									a(s, "situacao", "situacao", "false", "id_String", "VARCHAR", "true", "", "20",
											"0");
									return s;
								}

							}

							if (Processos_Ingestao_Bronze_1_row5 != null) {
								talendJobLog.addConnectionSchemaMessage("Processos_Ingestao_Bronze_1_tDBInput_5",
										"tPostgresqlInput", "Processos_Ingestao_Bronze_1_tFileOutputParquet_5",
										"tFileOutputParquet", "Processos_Ingestao_Bronze_1_row5" + iterateId,
										new SchemaUtil_Processos_Ingestao_Bronze_1_row5()
												.getSchema(Processos_Ingestao_Bronze_1_row5));
								talendJobLogProcess(globalMap);
								init_Processos_Ingestao_Bronze_1_tDBInput_5_0 = false;
							}

						}
						// QTUP-3575

						tos_count_Processos_Ingestao_Bronze_1_tDBInput_5++;

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_5 main ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_5 process_data_begin ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_5");

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_5 process_data_begin ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_5 main ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_5");

						if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

								, "Processos_Ingestao_Bronze_1_row5", "Processos_Ingestao_Bronze_1_tDBInput_5",
								"tDBInput_5", "tPostgresqlInput", "Processos_Ingestao_Bronze_1_tFileOutputParquet_5",
								"tFileOutputParquet_5", "tFileOutputParquet"

						)) {
							talendJobLogProcess(globalMap);
						}

						if (log.isTraceEnabled()) {
							log.trace("Processos_Ingestao_Bronze_1_row5 - "
									+ (Processos_Ingestao_Bronze_1_row5 == null ? ""
											: Processos_Ingestao_Bronze_1_row5.toLogString()));
						}

						org.talend.parquet.data.Group group_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = factory_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
								.newGroup();

						group_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.append("id_magistrado",
								Processos_Ingestao_Bronze_1_row5.id_magistrado);
						if (Processos_Ingestao_Bronze_1_row5.nome_completo != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.append("nome_completo",
									String.valueOf(Processos_Ingestao_Bronze_1_row5.nome_completo));
						}

						if (Processos_Ingestao_Bronze_1_row5.cargo != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.append("cargo",
									String.valueOf(Processos_Ingestao_Bronze_1_row5.cargo));
						}

						if (Processos_Ingestao_Bronze_1_row5.data_posse != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.append("data_posse",
									String.valueOf(Processos_Ingestao_Bronze_1_row5.data_posse));
						}

						if (Processos_Ingestao_Bronze_1_row5.situacao != null) {

							group_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.append("situacao",
									String.valueOf(Processos_Ingestao_Bronze_1_row5.situacao));
						}

						writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
								.write(group_Processos_Ingestao_Bronze_1_tFileOutputParquet_5);
						nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_5++;
						log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_5 - Writing the record "
								+ nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 + " to the file.");

						tos_count_Processos_Ingestao_Bronze_1_tFileOutputParquet_5++;

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_5 main ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_5 process_data_begin ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_5");

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_5 process_data_begin ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_5 process_data_end ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_5");

						/**
						 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_5 process_data_end ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_5 process_data_end ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_5");

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_5 process_data_end ] stop
						 */

						/**
						 * [Processos_Ingestao_Bronze_1_tDBInput_5 end ] start
						 */

						s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_5");

					}
				} finally {
					if (rs_Processos_Ingestao_Bronze_1_tDBInput_5 != null) {
						rs_Processos_Ingestao_Bronze_1_tDBInput_5.close();
					}
					if (stmt_Processos_Ingestao_Bronze_1_tDBInput_5 != null) {
						stmt_Processos_Ingestao_Bronze_1_tDBInput_5.close();
					}
					if (conn_Processos_Ingestao_Bronze_1_tDBInput_5 != null
							&& !conn_Processos_Ingestao_Bronze_1_tDBInput_5.isClosed()) {

						log.debug("Processos_Ingestao_Bronze_1_tDBInput_5 - Closing the connection to the database.");

						conn_Processos_Ingestao_Bronze_1_tDBInput_5.close();

						if ("com.mysql.cj.jdbc.Driver".equals((String) globalMap.get("driverClass_"))
								&& routines.system.BundleUtils.inOSGi()) {
							Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread")
									.getMethod("checkedShutdown").invoke(null, (Object[]) null);
						}

						log.debug("Processos_Ingestao_Bronze_1_tDBInput_5 - Connection to the database closed.");

					}

				}
				globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_5_NB_LINE",
						nb_line_Processos_Ingestao_Bronze_1_tDBInput_5);
				log.debug("Processos_Ingestao_Bronze_1_tDBInput_5 - Retrieved records count: "
						+ nb_line_Processos_Ingestao_Bronze_1_tDBInput_5 + " .");

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tDBInput_5 - " + ("Done."));

				ok_Hash.put("Processos_Ingestao_Bronze_1_tDBInput_5", true);
				end_Hash.put("Processos_Ingestao_Bronze_1_tDBInput_5", System.currentTimeMillis());

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_5 end ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_5 end ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_5");

				globalMap.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_5_NB_LINE",
						nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_5);

				log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_5 - Written records count: "
						+ nb_line_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 + " .");

				if (writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 != null) {
					writer_Processos_Ingestao_Bronze_1_tFileOutputParquet_5.close();
				}
				org.apache.hadoop.fs.FileSystem fs_Processos_Ingestao_Bronze_1_tFileOutputParquet_5 = crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
						.getFileSystem(config_Processos_Ingestao_Bronze_1_tFileOutputParquet_5);
				if (fs_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
						.exists(crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_5)) {
					fs_Processos_Ingestao_Bronze_1_tFileOutputParquet_5
							.delete(crcPath_Processos_Ingestao_Bronze_1_tFileOutputParquet_5, false);
				}

				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId,
						"Processos_Ingestao_Bronze_1_row5", 2, 0, "Processos_Ingestao_Bronze_1_tDBInput_5",
						"tDBInput_5", "tPostgresqlInput", "Processos_Ingestao_Bronze_1_tFileOutputParquet_5",
						"tFileOutputParquet_5", "tFileOutputParquet", "output")) {
					talendJobLogProcess(globalMap);
				}

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tFileOutputParquet_5 - " + ("Done."));

				ok_Hash.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_5", true);
				end_Hash.put("Processos_Ingestao_Bronze_1_tFileOutputParquet_5", System.currentTimeMillis());

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_5 end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException) && !(e instanceof TDieException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_5 finally ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tDBInput_5");

				/**
				 * [Processos_Ingestao_Bronze_1_tDBInput_5 finally ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_5 finally ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tFileOutputParquet_5");

				/**
				 * [Processos_Ingestao_Bronze_1_tFileOutputParquet_5 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("Processos_Ingestao_Bronze_1_tDBInput_5_SUBPROCESS_STATE", 1);
	}

	public void Processos_Ingestao_Bronze_1_tS3Connection_2Process(final java.util.Map<String, Object> globalMap)
			throws TalendException {
		globalMap.put("Processos_Ingestao_Bronze_1_tS3Connection_2_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("Processos_Ingestao_Bronze_1_tS3Connection_2", "6qzTGG_");

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				/**
				 * [Processos_Ingestao_Bronze_1_tS3Connection_2 begin ] start
				 */

				sh("Processos_Ingestao_Bronze_1_tS3Connection_2");

				s(currentComponent = "Processos_Ingestao_Bronze_1_tS3Connection_2");

				int tos_count_Processos_Ingestao_Bronze_1_tS3Connection_2 = 0;

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tS3Connection_2 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_Processos_Ingestao_Bronze_1_tS3Connection_2 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2 = new StringBuilder();
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2.append("Parameters:");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2
									.append("configuration.credentialProvider" + " = " + "STATIC_CREDENTIALS");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2
									.append("configuration.staticCredentialConfiguration.accessKey" + " = "
											+ "AKIAUWW4VRZLOLIPY4ZH");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2.append(
									"configuration.staticCredentialConfiguration.secretKey" + " = " + String.valueOf(
											"enc:routine.encryption.key.v1:ubUUl1AyE/xl6nAzDyJ4imTLZQMQ2MZWUWmc73kS7LomGO7nfGwqGROXQGedEBbuLJ4NO76j6fU2qEgYxVt1cSfp96w=")
											.substring(0, 4) + "...");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2
									.append("configuration.assumeRole" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2
									.append("configuration.region" + " = " + "DEFAULT");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2
									.append("configuration.clientSideEncrypt" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2
									.append("configuration.useRegionEndpoint" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2
									.append("configuration.configClient" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2
									.append("configuration.checkAccessibility" + " = " + "true");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2
									.append("configuration.checkMethod" + " = " + "BY_ACCOUNT_OWNER");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2.append(" | ");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2
									.append("configuration.enableAccelerate" + " = " + "false");
							log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2.append(" | ");
							if (log.isDebugEnabled())
								log.debug("Processos_Ingestao_Bronze_1_tS3Connection_2 - "
										+ (log4jParamters_Processos_Ingestao_Bronze_1_tS3Connection_2));
						}
					}
					new BytesLimit65535_Processos_Ingestao_Bronze_1_tS3Connection_2().limitLog4jByte();
				}
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_Processos_Ingestao_Bronze_1_tS3Connection_2 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter() throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("configuration.credentialProvider", "STATIC_CREDENTIALS");

								component_parameters.put("configuration.staticCredentialConfiguration.accessKey",
										"AKIAUWW4VRZLOLIPY4ZH");

								component_parameters.put("configuration.assumeRole", "false");

								component_parameters.put("configuration.region", "DEFAULT");

								component_parameters.put("configuration.clientSideEncrypt", "false");

								component_parameters.put("configuration.useRegionEndpoint", "false");

								component_parameters.put("configuration.configClient", "false");

								component_parameters.put("configuration.checkAccessibility", "true");

								component_parameters.put("configuration.checkMethod", "BY_ACCOUNT_OWNER");

								component_parameters.put("configuration.enableAccelerate", "false");

							} catch (java.lang.Exception e_Processos_Ingestao_Bronze_1_tS3Connection_2) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("Processos_Ingestao_Bronze_1_tS3Connection_2",
							"tS3Connection",
							new ParameterUtil_Processos_Ingestao_Bronze_1_tS3Connection_2().getParameter());
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("Processos_Ingestao_Bronze_1_tS3Connection_2", "tS3Connection_2",
							"tS3Connection");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				final org.talend.sdk.component.runtime.manager.ComponentManager mgr_Processos_Ingestao_Bronze_1_tS3Connection_2 = org.talend.sdk.component.runtime.manager.ComponentManager
						.instance();
				mgr_Processos_Ingestao_Bronze_1_tS3Connection_2.autoDiscoverPluginsIfEmpty(false, true);

				final java.util.Map<String, String> configuration_Processos_Ingestao_Bronze_1_tS3Connection_2 = new java.util.HashMap<>();
				final java.util.Map<String, String> registry_metadata_Processos_Ingestao_Bronze_1_tS3Connection_2 = new java.util.HashMap<>();

				final class SettingHelper_Processos_Ingestao_Bronze_1_tS3Connection_2 {
					final java.util.Map<String, String> configuration;

					SettingHelper_Processos_Ingestao_Bronze_1_tS3Connection_2(
							final java.util.Map<String, String> configuration) {
						this.configuration = configuration;
					}

					void put(String key, String value) {
						if (value != null) {
							configuration.put(key, value);
						}
					}
				}

				final SettingHelper_Processos_Ingestao_Bronze_1_tS3Connection_2 s_Processos_Ingestao_Bronze_1_tS3Connection_2 = new SettingHelper_Processos_Ingestao_Bronze_1_tS3Connection_2(
						configuration_Processos_Ingestao_Bronze_1_tS3Connection_2);
				Object dv_Processos_Ingestao_Bronze_1_tS3Connection_2;
				java.net.URL mappings_url_Processos_Ingestao_Bronze_1_tS3Connection_2 = this.getClass()
						.getResource("/xmlMappings");
				globalMap.put("Processos_Ingestao_Bronze_1_tS3Connection_2_MAPPINGS_URL",
						mappings_url_Processos_Ingestao_Bronze_1_tS3Connection_2);
				globalMap.putIfAbsent("TALEND_PRODUCT_VERSION", "8.0");
				globalMap.putIfAbsent("TALEND_AWS_TRACKER", "APN/1.0 Talend/8.0 Studio/8.0 (Qlik Talend Cloud)");

				s_Processos_Ingestao_Bronze_1_tS3Connection_2.put("configuration.credentialProvider",
						"STATIC_CREDENTIALS");

				s_Processos_Ingestao_Bronze_1_tS3Connection_2
						.put("configuration.staticCredentialConfiguration.accessKey", "AKIAUWW4VRZLOLIPY4ZH");
				s_Processos_Ingestao_Bronze_1_tS3Connection_2.put(
						"configuration.staticCredentialConfiguration.secretKey",
						routines.system.PasswordEncryptUtil.decryptPassword(
								"enc:routine.encryption.key.v1:GCR2aYEGDUMtX/Y6oFjpmBQBeYcnrCqLQTC65JhJasbzgmNj4fc1aSYRMPCpsZrnR5JZL/r+ACc/FZ2nyi2rcPMU5mg="));

				s_Processos_Ingestao_Bronze_1_tS3Connection_2.put("configuration.assumeRole", "false");

				s_Processos_Ingestao_Bronze_1_tS3Connection_2.put("configuration.region", "DEFAULT");

				s_Processos_Ingestao_Bronze_1_tS3Connection_2.put("configuration.clientSideEncrypt", "false");

				s_Processos_Ingestao_Bronze_1_tS3Connection_2.put("configuration.useRegionEndpoint", "false");

				s_Processos_Ingestao_Bronze_1_tS3Connection_2.put("configuration.configClient", "false");

				s_Processos_Ingestao_Bronze_1_tS3Connection_2.put("configuration.checkAccessibility", "true");

				s_Processos_Ingestao_Bronze_1_tS3Connection_2.put("configuration.checkMethod", "BY_ACCOUNT_OWNER");

				s_Processos_Ingestao_Bronze_1_tS3Connection_2.put("configuration.enableAccelerate", "false");

				s_Processos_Ingestao_Bronze_1_tS3Connection_2.put("configuration.__version", "-1");
				final class SchemaSettingHelper_Processos_Ingestao_Bronze_1_tS3Connection_2_1 {

					public void set(final java.util.Map<String, String> configuration) throws java.lang.Exception {
						set_0(configuration);
					}

					public void set_0(final java.util.Map<String, String> configuration) throws java.lang.Exception {
						configuration.put("SCHEMA_FLOW[0]", "CURRENT_BUCKET");
						configuration.put("SCHEMA_FLOW[1]", "CURRENT_KEY");
						configuration.put("SCHEMA_FLOW[2]", "CURRENT_SIZE");
						configuration.put("SCHEMA_FLOW[3]", "CURRENT_LASTMODIFIED");
						configuration.put("SCHEMA_FLOW[4]", "CURRENT_OWNER");
						configuration.put("SCHEMA_FLOW[5]", "CURRENT_OWNER_ID");
						configuration.put("SCHEMA_FLOW[6]", "CURRENT_ETAG");
						configuration.put("SCHEMA_FLOW[7]", "CURRENT_STORAGECLASS");
					}
				}
				new SchemaSettingHelper_Processos_Ingestao_Bronze_1_tS3Connection_2_1()
						.set(configuration_Processos_Ingestao_Bronze_1_tS3Connection_2);

				mgr_Processos_Ingestao_Bronze_1_tS3Connection_2.findPlugin("aws-s3")
						.orElseThrow(() -> new IllegalStateException("Can't find the plugin : aws-s3"))
						.get(org.talend.sdk.component.runtime.manager.ContainerComponentRegistry.class).getServices()
						.stream().forEach(serviceMeta_Processos_Ingestao_Bronze_1_tS3Connection_2 -> {
							serviceMeta_Processos_Ingestao_Bronze_1_tS3Connection_2.getActions().stream().filter(
									actionMeta_Processos_Ingestao_Bronze_1_tS3Connection_2 -> "create_connection"
											.equals(actionMeta_Processos_Ingestao_Bronze_1_tS3Connection_2.getType()))
									.forEach(actionMeta_Processos_Ingestao_Bronze_1_tS3Connection_2 -> {
										synchronized (serviceMeta_Processos_Ingestao_Bronze_1_tS3Connection_2
												.getInstance()) {
											org.talend.sdk.component.runtime.di.studio.RuntimeContextInjector
													.injectService(mgr_Processos_Ingestao_Bronze_1_tS3Connection_2,
															"aws-s3",
															new org.talend.sdk.component.api.context.RuntimeContextHolder(
																	"Processos_Ingestao_Bronze_1_tS3Connection_2",
																	globalMap));

											Object connnection_Processos_Ingestao_Bronze_1_tS3Connection_2 = actionMeta_Processos_Ingestao_Bronze_1_tS3Connection_2
													.getInvoker()
													.apply(configuration_Processos_Ingestao_Bronze_1_tS3Connection_2);

											globalMap.put("conn_Processos_Ingestao_Bronze_1_tS3Connection_2",
													connnection_Processos_Ingestao_Bronze_1_tS3Connection_2);

											try {
												configuration_Processos_Ingestao_Bronze_1_tS3Connection_2.put(
														"configuration.staticCredentialConfiguration.secretKey",
														routines.system.PasswordEncryptUtil.encryptPassword(
																configuration_Processos_Ingestao_Bronze_1_tS3Connection_2
																		.get("configuration.staticCredentialConfiguration.secretKey")));
											} catch (Exception e) {
												e.printStackTrace();
											}
											globalMap.put("configuration_Processos_Ingestao_Bronze_1_tS3Connection_2",
													configuration_Processos_Ingestao_Bronze_1_tS3Connection_2);
										}
									});
						});

				/**
				 * [Processos_Ingestao_Bronze_1_tS3Connection_2 begin ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tS3Connection_2 main ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tS3Connection_2");

				tos_count_Processos_Ingestao_Bronze_1_tS3Connection_2++;

				/**
				 * [Processos_Ingestao_Bronze_1_tS3Connection_2 main ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tS3Connection_2 process_data_begin ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tS3Connection_2");

				/**
				 * [Processos_Ingestao_Bronze_1_tS3Connection_2 process_data_begin ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tS3Connection_2 process_data_end ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tS3Connection_2");

				/**
				 * [Processos_Ingestao_Bronze_1_tS3Connection_2 process_data_end ] stop
				 */

				/**
				 * [Processos_Ingestao_Bronze_1_tS3Connection_2 end ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tS3Connection_2");

				if (log.isDebugEnabled())
					log.debug("Processos_Ingestao_Bronze_1_tS3Connection_2 - " + ("Done."));

				ok_Hash.put("Processos_Ingestao_Bronze_1_tS3Connection_2", true);
				end_Hash.put("Processos_Ingestao_Bronze_1_tS3Connection_2", System.currentTimeMillis());

				if (execStat) {
					runStat.updateStatOnConnection("Processos_Ingestao_Bronze_1_OnComponentOk2", 0, "ok");
				}
				Processos_Ingestao_Bronze_1_tDBInput_1Process(globalMap);

				/**
				 * [Processos_Ingestao_Bronze_1_tS3Connection_2 end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException) && !(e instanceof TDieException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [Processos_Ingestao_Bronze_1_tS3Connection_2 finally ] start
				 */

				s(currentComponent = "Processos_Ingestao_Bronze_1_tS3Connection_2");

				/**
				 * [Processos_Ingestao_Bronze_1_tS3Connection_2 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("Processos_Ingestao_Bronze_1_tS3Connection_2_SUBPROCESS_STATE", 1);
	}

	public void talendJobLogProcess(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("talendJobLog_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				/**
				 * [talendJobLog begin ] start
				 */

				sh("talendJobLog");

				s(currentComponent = "talendJobLog");

				int tos_count_talendJobLog = 0;

				for (JobStructureCatcherUtils.JobStructureCatcherMessage jcm : talendJobLog.getMessages()) {
					org.talend.job.audit.JobContextBuilder builder_talendJobLog = org.talend.job.audit.JobContextBuilder
							.create().jobName(jcm.job_name).jobId(jcm.job_id).jobVersion(jcm.job_version)
							.custom("process_id", jcm.pid).custom("thread_id", jcm.tid).custom("pid", pid)
							.custom("father_pid", fatherPid).custom("root_pid", rootPid);
					org.talend.logging.audit.Context log_context_talendJobLog = null;

					if (jcm.log_type == JobStructureCatcherUtils.LogType.PERFORMANCE) {
						long timeMS = jcm.end_time - jcm.start_time;
						String duration = String.valueOf(timeMS);

						log_context_talendJobLog = builder_talendJobLog.sourceId(jcm.sourceId)
								.sourceLabel(jcm.sourceLabel).sourceConnectorType(jcm.sourceComponentName)
								.targetId(jcm.targetId).targetLabel(jcm.targetLabel)
								.targetConnectorType(jcm.targetComponentName).connectionName(jcm.current_connector)
								.rows(jcm.row_count).duration(duration).build();
						auditLogger_talendJobLog.flowExecution(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.JOBSTART) {
						log_context_talendJobLog = builder_talendJobLog.timestamp(jcm.moment).build();
						auditLogger_talendJobLog.jobstart(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.JOBEND) {
						long timeMS = jcm.end_time - jcm.start_time;
						String duration = String.valueOf(timeMS);

						log_context_talendJobLog = builder_talendJobLog.timestamp(jcm.moment).duration(duration)
								.status(jcm.status).build();
						auditLogger_talendJobLog.jobstop(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.RUNCOMPONENT) {
						log_context_talendJobLog = builder_talendJobLog.timestamp(jcm.moment)
								.connectorType(jcm.component_name).connectorId(jcm.component_id)
								.connectorLabel(jcm.component_label).build();
						auditLogger_talendJobLog.runcomponent(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.FLOWINPUT) {// log current component
																							// input line
						long timeMS = jcm.end_time - jcm.start_time;
						String duration = String.valueOf(timeMS);

						log_context_talendJobLog = builder_talendJobLog.connectorType(jcm.component_name)
								.connectorId(jcm.component_id).connectorLabel(jcm.component_label)
								.connectionName(jcm.current_connector).connectionType(jcm.current_connector_type)
								.rows(jcm.total_row_number).duration(duration).build();
						auditLogger_talendJobLog.flowInput(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.FLOWOUTPUT) {// log current component
																								// output/reject line
						long timeMS = jcm.end_time - jcm.start_time;
						String duration = String.valueOf(timeMS);

						log_context_talendJobLog = builder_talendJobLog.connectorType(jcm.component_name)
								.connectorId(jcm.component_id).connectorLabel(jcm.component_label)
								.connectionName(jcm.current_connector).connectionType(jcm.current_connector_type)
								.rows(jcm.total_row_number).duration(duration).build();
						auditLogger_talendJobLog.flowOutput(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.JOBERROR) {
						java.lang.Exception e_talendJobLog = jcm.exception;
						if (e_talendJobLog != null) {
							try (java.io.StringWriter sw_talendJobLog = new java.io.StringWriter();
									java.io.PrintWriter pw_talendJobLog = new java.io.PrintWriter(sw_talendJobLog)) {
								e_talendJobLog.printStackTrace(pw_talendJobLog);
								builder_talendJobLog.custom("stacktrace", sw_talendJobLog.getBuffer().substring(0,
										java.lang.Math.min(sw_talendJobLog.getBuffer().length(), 512)));
							}
						}

						if (jcm.extra_info != null) {
							builder_talendJobLog.connectorId(jcm.component_id).custom("extra_info", jcm.extra_info);
						}

						log_context_talendJobLog = builder_talendJobLog
								.connectorType(jcm.component_id.substring(0, jcm.component_id.lastIndexOf('_')))
								.connectorId(jcm.component_id)
								.connectorLabel(jcm.component_label == null ? jcm.component_id : jcm.component_label)
								.build();

						auditLogger_talendJobLog.exception(log_context_talendJobLog);
					}

					if (jcm.log_type == JobStructureCatcherUtils.LogType.RUNTIMEPARAMETER) {
						builder_talendJobLog.connectorType(jcm.component_name).connectorId(jcm.component_id);

						for (java.util.Map.Entry<String, String> entry : jcm.component_parameters.entrySet()) {
							builder_talendJobLog.custom("P_" + entry.getKey(), entry.getValue());
						}

						log_context_talendJobLog = builder_talendJobLog.build();

						runtime_lineage_logger_talendJobLog.componentParameters(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.RUNTIMESCHEMA) {
						builder_talendJobLog.sourceConnectorType(jcm.sourceComponentName).sourceId(jcm.sourceId)
								.connectionName(jcm.current_connector).schema(jcm.component_schema.toString())
								.targetConnectorType(jcm.targetComponentName).targetId(jcm.targetId);

						if (jcm.current_connector_type != null) {
							builder_talendJobLog.custom("lineStyle", jcm.current_connector_type);
						}
						log_context_talendJobLog = builder_talendJobLog.build();
						runtime_lineage_logger_talendJobLog.schema(log_context_talendJobLog);
					}

				}

				/**
				 * [talendJobLog begin ] stop
				 */

				/**
				 * [talendJobLog main ] start
				 */

				s(currentComponent = "talendJobLog");

				tos_count_talendJobLog++;

				/**
				 * [talendJobLog main ] stop
				 */

				/**
				 * [talendJobLog process_data_begin ] start
				 */

				s(currentComponent = "talendJobLog");

				/**
				 * [talendJobLog process_data_begin ] stop
				 */

				/**
				 * [talendJobLog process_data_end ] start
				 */

				s(currentComponent = "talendJobLog");

				/**
				 * [talendJobLog process_data_end ] stop
				 */

				/**
				 * [talendJobLog end ] start
				 */

				s(currentComponent = "talendJobLog");

				ok_Hash.put("talendJobLog", true);
				end_Hash.put("talendJobLog", System.currentTimeMillis());

				/**
				 * [talendJobLog end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException) && !(e instanceof TDieException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [talendJobLog finally ] start
				 */

				s(currentComponent = "talendJobLog");

				/**
				 * [talendJobLog finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("talendJobLog_SUBPROCESS_STATE", 1);
	}

	public String resuming_logs_dir_path = null;
	public String resuming_checkpoint_path = null;
	public String parent_part_launcher = null;
	private String resumeEntryMethodName = null;
	private boolean globalResumeTicket = false;

	public boolean watch = false;
	// portStats is null, it means don't execute the statistics
	public Integer portStats = null;
	public int portTraces = 4334;
	public String clientHost;
	public String defaultClientHost = "localhost";
	public String contextStr = "Default";
	public boolean isDefaultContext = true;
	public String pid = "0";
	public String rootPid = null;
	public String fatherPid = null;
	public String fatherNode = null;
	public long startTime = 0;
	public boolean isChildJob = false;
	public String log4jLevel = "";

	private boolean enableLogStash;
	private boolean enableLineage;

	private boolean execStat = true;

	private ThreadLocal<java.util.Map<String, String>> threadLocal = new ThreadLocal<java.util.Map<String, String>>() {
		protected java.util.Map<String, String> initialValue() {
			java.util.Map<String, String> threadRunResultMap = new java.util.HashMap<String, String>();
			threadRunResultMap.put("errorCode", null);
			threadRunResultMap.put("status", "");
			return threadRunResultMap;
		};
	};

	protected PropertiesWithType context_param = new PropertiesWithType();
	public java.util.Map<String, Object> parentContextMap = new java.util.HashMap<String, Object>();

	public String status = "";

	private final static java.util.Properties jobInfo = new java.util.Properties();
	private final static java.util.Map<String, String> mdcInfo = new java.util.HashMap<>();
	private final static java.util.concurrent.atomic.AtomicLong subJobPidCounter = new java.util.concurrent.atomic.AtomicLong();

	public static void main(String[] args) {
		final TRF5 TRF5Class = new TRF5();

		int exitCode = TRF5Class.runJobInTOS(args);
		if (exitCode == 0) {
			log.info("TalendJob: 'TRF5' - Done.");
		}

		System.exit(exitCode);
	}

	private void getjobInfo() {
		final String TEMPLATE_PATH = "src/main/templates/jobInfo_template.properties";
		final String BUILD_PATH = "../jobInfo.properties";
		final String path = this.getClass().getResource("").getPath();
		if (path.lastIndexOf("target") > 0) {
			final java.io.File templateFile = new java.io.File(
					path.substring(0, path.lastIndexOf("target")).concat(TEMPLATE_PATH));
			if (templateFile.exists()) {
				readJobInfo(templateFile);
				return;
			}
		}
		readJobInfo(new java.io.File(BUILD_PATH));
	}

	private void readJobInfo(java.io.File jobInfoFile) {

		if (jobInfoFile.exists()) {
			try (java.io.InputStream is = new java.io.FileInputStream(jobInfoFile)) {
				jobInfo.load(is);
			} catch (IOException e) {

				log.debug("Read jobInfo.properties file fail: " + e.getMessage());

			}
		}
		log.info(String.format("Project name: %s\tJob name: %s\tGIT Commit ID: %s\tTalend Version: %s", projectName,
				jobName, jobInfo.getProperty("gitCommitId"), "8.0.1.20260211_0926-patch"));

	}

	public String[][] runJob(String[] args) {

		int exitCode = runJobInTOS(args);
		String[][] bufferValue = new String[][] { { Integer.toString(exitCode) } };

		return bufferValue;
	}

	public boolean hastBufferOutputComponent() {
		boolean hastBufferOutput = false;

		return hastBufferOutput;
	}

	public int runJobInTOS(String[] args) {
		// reset status
		status = "";

		String lastStr = "";
		for (String arg : args) {
			if (arg.equalsIgnoreCase("--context_param")) {
				lastStr = arg;
			} else if (lastStr.equals("")) {
				evalParam(arg);
			} else {
				evalParam(lastStr + " " + arg);
				lastStr = "";
			}
		}

		final boolean enableCBP = false;
		boolean inOSGi = routines.system.BundleUtils.inOSGi();

		boolean needSendForCBP = false;
		if (!inOSGi && isCBPClientPresent) {
			if (org.talend.metrics.CBPClient.getInstanceForCurrentVM() == null) {
				try {
					needSendForCBP = true;
					org.talend.metrics.CBPClient.startListenIfNotStarted(enableCBP, true);
				} catch (java.lang.Exception e) {
					errorCode = 1;
					status = "failure";
					e.printStackTrace();
					return 1;
				}
			}
		}

		enableLogStash = "true".equalsIgnoreCase(System.getProperty("audit.enabled"));

		if (!"".equals(log4jLevel)) {

			if ("trace".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.TRACE);
			} else if ("debug".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.DEBUG);
			} else if ("info".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.INFO);
			} else if ("warn".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.WARN);
			} else if ("error".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.ERROR);
			} else if ("fatal".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.FATAL);
			} else if ("off".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.OFF);
			}
			org.apache.logging.log4j.core.config.Configurator
					.setLevel(org.apache.logging.log4j.LogManager.getRootLogger().getName(), log.getLevel());

		}

		getjobInfo();
		log.info("TalendJob: 'TRF5' - Start.");

		java.util.Set<Object> jobInfoKeys = jobInfo.keySet();
		for (Object jobInfoKey : jobInfoKeys) {
			org.slf4j.MDC.put("_" + jobInfoKey.toString(), jobInfo.get(jobInfoKey).toString());
		}
		org.slf4j.MDC.put("_pid", pid);
		org.slf4j.MDC.put("_rootPid", rootPid);
		org.slf4j.MDC.put("_fatherPid", fatherPid);
		org.slf4j.MDC.put("_projectName", projectName);
		org.slf4j.MDC.put("_startTimestamp", java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
				.format(java.time.format.DateTimeFormatter.ISO_INSTANT));
		org.slf4j.MDC.put("_jobRepositoryId", "_mceAMB7SEfG99Y5mkdgJFg");
		org.slf4j.MDC.put("_compiledAtTimestamp", "2026-03-17T04:47:40.471942800Z");

		java.lang.management.RuntimeMXBean mx = java.lang.management.ManagementFactory.getRuntimeMXBean();
		String[] mxNameTable = mx.getName().split("@"); //$NON-NLS-1$
		if (mxNameTable.length == 2) {
			org.slf4j.MDC.put("_systemPid", mxNameTable[0]);
		} else {
			org.slf4j.MDC.put("_systemPid", String.valueOf(java.lang.Thread.currentThread().getId()));
		}

		final String lineageDirPath = System.getProperty("runtime.lineage.outputpath");
		final String payloadFilePath = System.getProperty("tmc.task.payload.path");
		if ((lineageDirPath != null && !lineageDirPath.isEmpty())
				|| (payloadFilePath != null && !payloadFilePath.isEmpty())) {
			enableLineage = true;

			java.util.Properties p_talendJobLog = new java.util.Properties();
			p_talendJobLog.setProperty("root.logger", "runtime");
			p_talendJobLog.setProperty("encoding", "UTF-8");
			p_talendJobLog.setProperty("application.name", "Talend Studio");
			p_talendJobLog.setProperty("service.name", "Talend Studio Job");
			p_talendJobLog.setProperty("instance.name", "Talend Studio Job Instance");
			p_talendJobLog.setProperty("propagate.appender.exceptions", "none");
			p_talendJobLog.setProperty("log.appender", "file");
			p_talendJobLog.setProperty("appender.file.path", "runtime.json");
			p_talendJobLog.setProperty("appender.file.maxsize", "52428800");
			p_talendJobLog.setProperty("appender.file.maxbackup", "20");
			p_talendJobLog.setProperty("host", "false");

			final String lineageFilePath = System.getProperty("runtime.lineage.appender.file.path");
			if (lineageFilePath == null || lineageFilePath.isEmpty()) {
				String finalLineageDirPath = lineageDirPath;
				if (lineageDirPath == null || lineageDirPath.isEmpty()) {
					finalLineageDirPath = payloadFilePath + "_dir";
				}

				System.setProperty("runtime.lineage.appender.file.path",
						new StringBuilder().append(finalLineageDirPath)
								.append((finalLineageDirPath.endsWith("/") || finalLineageDirPath.endsWith("\\")) ? ""
										: java.io.File.separator)
								.append(projectName).append(java.io.File.separatorChar).append(jobName)
								.append(java.io.File.separatorChar).append(jobVersion)
								.append(java.io.File.separatorChar).append("runtime_log_")
								.append(new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()))
								.append(".json").toString());
			}

			System.getProperties().stringPropertyNames().stream()
					.filter(it -> it.startsWith("runtime.lineage.") && !"runtime.lineage.outputpath".equals(it))
					.forEach(key -> p_talendJobLog.setProperty(key.substring("runtime.lineage.".length()),
							System.getProperty(key)));

			org.apache.logging.log4j.core.config.Configurator.setLevel(p_talendJobLog.getProperty("root.logger"),
					org.apache.logging.log4j.Level.DEBUG);

			runtime_lineage_logger_talendJobLog = org.talend.job.audit.JobEventAuditLoggerFactory
					.createJobAuditLogger(p_talendJobLog);
		}

		if (enableLogStash) {
			java.util.Properties properties_talendJobLog = new java.util.Properties();
			properties_talendJobLog.setProperty("root.logger", "audit");
			properties_talendJobLog.setProperty("encoding", "UTF-8");
			properties_talendJobLog.setProperty("application.name", "Talend Studio");
			properties_talendJobLog.setProperty("service.name", "Talend Studio Job");
			properties_talendJobLog.setProperty("instance.name", "Talend Studio Job Instance");
			properties_talendJobLog.setProperty("propagate.appender.exceptions", "none");
			properties_talendJobLog.setProperty("log.appender", "file");
			properties_talendJobLog.setProperty("appender.file.path", "audit.json");
			properties_talendJobLog.setProperty("appender.file.maxsize", "52428800");
			properties_talendJobLog.setProperty("appender.file.maxbackup", "20");
			properties_talendJobLog.setProperty("host", "false");

			System.getProperties().stringPropertyNames().stream().filter(it -> it.startsWith("audit.logger."))
					.forEach(key -> properties_talendJobLog.setProperty(key.substring("audit.logger.".length()),
							System.getProperty(key)));

			org.apache.logging.log4j.core.config.Configurator
					.setLevel(properties_talendJobLog.getProperty("root.logger"), org.apache.logging.log4j.Level.DEBUG);

			auditLogger_talendJobLog = org.talend.job.audit.JobEventAuditLoggerFactory
					.createJobAuditLogger(properties_talendJobLog);
		}

		if (clientHost == null) {
			clientHost = defaultClientHost;
		}

		if (pid == null || "0".equals(pid)) {
			pid = TalendString.getAsciiRandomString(6);
		}

		org.slf4j.MDC.put("_pid", pid);

		if (rootPid == null) {
			rootPid = pid;
		}

		org.slf4j.MDC.put("_rootPid", rootPid);

		if (fatherPid == null) {
			fatherPid = pid;
		} else {
			isChildJob = true;
		}
		org.slf4j.MDC.put("_fatherPid", fatherPid);

		if (portStats != null) {
			// portStats = -1; //for testing
			if (portStats < 0 || portStats > 65535) {
				// issue:10869, the portStats is invalid, so this client socket can't open
				System.err.println("The statistics socket port " + portStats + " is invalid.");
				execStat = false;
			}
		} else {
			execStat = false;
		}

		try {
			java.util.Dictionary<String, Object> jobProperties = null;
			if (inOSGi) {
				jobProperties = routines.system.BundleUtils.getJobProperties(jobName);

				if (jobProperties != null && jobProperties.get("context") != null) {
					contextStr = (String) jobProperties.get("context");
				}

				if (jobProperties != null && jobProperties.get("taskExecutionId") != null) {
					taskExecutionId = (String) jobProperties.get("taskExecutionId");
				}

				// extract ids from parent route
				if (null == taskExecutionId || taskExecutionId.isEmpty()) {
					for (String arg : args) {
						if (arg.startsWith("--context_param")
								&& (arg.contains("taskExecutionId") || arg.contains("jobExecutionId"))) {

							String keyValue = arg.replace("--context_param", "");
							String[] parts = keyValue.split("=");
							String[] cleanParts = java.util.Arrays.stream(parts).filter(s -> !s.isEmpty())
									.toArray(String[]::new);
							if (cleanParts.length == 2) {
								String key = cleanParts[0];
								String value = cleanParts[1];
								if ("taskExecutionId".equals(key.trim()) && null != value) {
									taskExecutionId = value.trim();
								} else if ("jobExecutionId".equals(key.trim()) && null != value) {
									jobExecutionId = value.trim();
								}
							}
						}
					}
				}
			}

			// first load default key-value pairs from application.properties
			if (isStandaloneMS) {
				context.putAll(this.getDefaultProperties());
			}
			// call job/subjob with an existing context, like: --context=production. if
			// without this parameter, there will use the default context instead.
			java.io.InputStream inContext = TRF5.class.getClassLoader()
					.getResourceAsStream("demotrf5/trf5_0_1/contexts/" + contextStr + ".properties");
			if (inContext == null) {
				inContext = TRF5.class.getClassLoader()
						.getResourceAsStream("config/contexts/" + contextStr + ".properties");
			}
			if (inContext != null) {
				try {
					// defaultProps is in order to keep the original context value
					if (context != null && context.isEmpty()) {
						defaultProps.load(inContext);
						if (inOSGi && jobProperties != null) {
							java.util.Enumeration<String> keys = jobProperties.keys();
							while (keys.hasMoreElements()) {
								String propKey = keys.nextElement();
								if (defaultProps.containsKey(propKey)) {
									defaultProps.put(propKey, (String) jobProperties.get(propKey));
								}
							}
						}
						context = new ContextProperties(defaultProps);
					}
					if (isStandaloneMS) {
						// override context key-value pairs if provided using --context=contextName
						defaultProps.load(inContext);
						context.putAll(defaultProps);
					}
				} finally {
					inContext.close();
				}
			} else if (!isDefaultContext) {
				// print info and job continue to run, for case: context_param is not empty.
				System.err.println("Could not find the context " + contextStr);
			}
			// override key-value pairs if provided via --config.location=file1.file2 OR
			// --config.additional-location=file1,file2
			if (isStandaloneMS) {
				context.putAll(this.getAdditionalProperties());
			}

			// override key-value pairs if provide via command line like
			// --key1=value1,--key2=value2
			if (!context_param.isEmpty()) {
				context.putAll(context_param);
				// set types for params from parentJobs
				for (Object key : context_param.keySet()) {
					String context_key = key.toString();
					String context_type = context_param.getContextType(context_key);
					context.setContextType(context_key, context_type);

				}
			}
			class ContextProcessing {
				private void processContext_0() {
				}

				public void processAllContext() {
					processContext_0();
				}
			}

			new ContextProcessing().processAllContext();
		} catch (java.io.IOException ie) {
			System.err.println("Could not load context " + contextStr);
			ie.printStackTrace();
		}

		// get context value from parent directly
		if (parentContextMap != null && !parentContextMap.isEmpty()) {
		}

		// Resume: init the resumeUtil
		resumeEntryMethodName = ResumeUtil.getResumeEntryMethodName(resuming_checkpoint_path);
		resumeUtil = new ResumeUtil(resuming_logs_dir_path, isChildJob, rootPid);
		resumeUtil.initCommonInfo(pid, rootPid, fatherPid, projectName, jobName, contextStr, jobVersion);

		List<String> parametersToEncrypt = new java.util.ArrayList<String>();
		// Resume: jobStart
		resumeUtil.addLog("JOB_STARTED", "JOB:" + jobName, parent_part_launcher, Thread.currentThread().getId() + "",
				"", "", "", "", ResumeUtil.convertToJsonText(context, ContextProperties.class, parametersToEncrypt));

		org.slf4j.MDC.put("_context", contextStr);
		log.info("TalendJob: 'TRF5' - Started.");
		java.util.Optional.ofNullable(org.slf4j.MDC.getCopyOfContextMap()).ifPresent(mdcInfo::putAll);

		if (execStat) {
			try {
				runStat.openSocket(!isChildJob);
				runStat.setAllPID(rootPid, fatherPid, pid, jobName);
				runStat.startThreadStat(clientHost, portStats);
				runStat.updateStatOnJob(RunStat.JOBSTART, fatherNode);
			} catch (java.io.IOException ioException) {
				ioException.printStackTrace();
			}
		}

		java.util.concurrent.ConcurrentHashMap<Object, Object> concurrentHashMap = new java.util.concurrent.ConcurrentHashMap<Object, Object>();
		globalMap.put("concurrentHashMap", concurrentHashMap);

		long startUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long endUsedMemory = 0;
		long end = 0;

		startTime = System.currentTimeMillis();

		this.globalResumeTicket = true;// to run tPreJob

		if (enableLogStash) {
			talendJobLog.addJobStartMessage();
			try {
				talendJobLogProcess(globalMap);
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}

		this.globalResumeTicket = false;// to run others jobs

		try {
			errorCode = null;
			Processo_Silver_1_tS3Connection_1Process(globalMap);
			if (!"failure".equals(status)) {
				status = "end";
			}
		} catch (TalendException e_Processo_Silver_1_tS3Connection_1) {
			globalMap.put("Processo_Silver_1_tS3Connection_1_SUBPROCESS_STATE", -1);

			e_Processo_Silver_1_tS3Connection_1.printStackTrace();

		}
		try {
			errorCode = null;
			Processos_Ingestao_Bronze_1_tS3Connection_2Process(globalMap);
			if (!"failure".equals(status)) {
				status = "end";
			}
		} catch (TalendException e_Processos_Ingestao_Bronze_1_tS3Connection_2) {
			globalMap.put("Processos_Ingestao_Bronze_1_tS3Connection_2_SUBPROCESS_STATE", -1);

			e_Processos_Ingestao_Bronze_1_tS3Connection_2.printStackTrace();

		}

		this.globalResumeTicket = true;// to run tPostJob

		end = System.currentTimeMillis();

		if (watch) {
			System.out.println((end - startTime) + " milliseconds");
		}

		endUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		if (false) {
			System.out.println((endUsedMemory - startUsedMemory) + " bytes memory increase when running : TRF5");
		}
		if (enableLogStash) {
			talendJobLog.addJobEndMessage(startTime, end, status);
			try {
				talendJobLogProcess(globalMap);
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}

		if (execStat) {
			runStat.updateStatOnJob(RunStat.JOBEND, fatherNode);
			runStat.stopThreadStat();
		}
		if (!inOSGi && isCBPClientPresent) {
			if (needSendForCBP && org.talend.metrics.CBPClient.getInstanceForCurrentVM() != null) {
				s("none");
				org.talend.metrics.CBPClient.getInstanceForCurrentVM().sendData();
			}
		}

		if (!isChildJob) {
			// TODO TMC need to make sure the key or provide a way to pass it to job,
			// consider java command length too long risk in windows OS
			String taskId = System.getProperty("tmc.task.id");// not the same key with CBP
			String taskVersion = System.getProperty("tmc.task.version");
			String taskExecId = System.getProperty("tmc.task.execution.id");
			String lineageFilePath = System.getProperty("runtime.lineage.appender.file.path");
			try {
				if (lineageFilePath != null && payloadFilePath != null) {
					org.talend.studio.ProducerHelper.newPayload(new java.io.File(payloadFilePath),
							new java.io.File(lineageFilePath), taskExecId, taskId, taskVersion,
							lineageDirPath == null ? true : false);
				}
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}

		int returnCode = 0;

		if (errorCode == null) {
			returnCode = status != null && status.equals("failure") ? 1 : 0;
		} else {
			returnCode = errorCode.intValue();
		}
		resumeUtil.addLog("JOB_ENDED", "JOB:" + jobName, parent_part_launcher, Thread.currentThread().getId() + "", "",
				"" + returnCode, "", "", "");
		resumeUtil.flush();

		org.slf4j.MDC.remove("_subJobName");
		org.slf4j.MDC.remove("_subJobPid");
		org.slf4j.MDC.remove("_systemPid");
		log.info("TalendJob: 'TRF5' - Finished - status: " + status + " returnCode: " + returnCode);

		return returnCode;

	}

	// only for OSGi env
	public void destroy() {
		// add CBP code for OSGI Executions
		if (null != taskExecutionId && !taskExecutionId.isEmpty()) {
			try {
				org.talend.metrics.DataReadTracker.setExecutionId(taskExecutionId, jobExecutionId, false);
				org.talend.metrics.DataReadTracker.sealCounter();
				org.talend.metrics.DataReadTracker.reset();
			} catch (Exception | NoClassDefFoundError e) {
				// ignore
			}
		}

		// check for orphan threads if still alive after undeploy
		synchronized (threadList) {
			java.util.Iterator<Thread> it = threadList.iterator();
			while (it.hasNext()) {
				Thread thread = it.next();
				if (thread != null && thread.isAlive()) {
					System.err.println(
							"Initiating thread cleanup prior to bundle undeployment. This is a precautionary step to ensure no no memory leaks.");
					System.err.println("Forcefully interrupting thread with ID = " + thread.getId()
							+ ". This may result in expected errors due to abrupt termination. Please verify if the thread was performing critical operations.");
					thread.interrupt();
				}
				if (thread == null || !thread.isAlive()) {
					it.remove();
				}
			}
		}
		// end of destroy()
	}

	private java.util.Map<String, Object> getSharedConnections4REST() {
		java.util.Map<String, Object> connections = new java.util.HashMap<String, Object>();

		connections.put("conn_Processo_Silver_1_tS3Connection_1",
				globalMap.get("conn_Processo_Silver_1_tS3Connection_1"));
		connections.put("conn_Processos_Ingestao_Bronze_1_tS3Connection_2",
				globalMap.get("conn_Processos_Ingestao_Bronze_1_tS3Connection_2"));

		return connections;
	}

	private void evalParam(String arg) {
		if (arg.startsWith("--resuming_logs_dir_path")) {
			resuming_logs_dir_path = arg.substring(25);
		} else if (arg.startsWith("--resuming_checkpoint_path")) {
			resuming_checkpoint_path = arg.substring(27);
		} else if (arg.startsWith("--parent_part_launcher")) {
			parent_part_launcher = arg.substring(23);
		} else if (arg.startsWith("--watch")) {
			watch = true;
		} else if (arg.startsWith("--stat_port=")) {
			String portStatsStr = arg.substring(12);
			if (portStatsStr != null && !portStatsStr.equals("null")) {
				portStats = Integer.parseInt(portStatsStr);
			}
		} else if (arg.startsWith("--trace_port=")) {
			portTraces = Integer.parseInt(arg.substring(13));
		} else if (arg.startsWith("--client_host=")) {
			clientHost = arg.substring(14);
		} else if (arg.startsWith("--context=")) {
			contextStr = arg.substring(10);
			isDefaultContext = false;
		} else if (arg.startsWith("--father_pid=")) {
			fatherPid = arg.substring(13);
		} else if (arg.startsWith("--root_pid=")) {
			rootPid = arg.substring(11);
		} else if (arg.startsWith("--father_node=")) {
			fatherNode = arg.substring(14);
		} else if (arg.startsWith("--pid=")) {
			pid = arg.substring(6);
		} else if (arg.startsWith("--context_type")) {
			String keyValue = arg.substring(15);
			int index = -1;
			if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {
				if (fatherPid == null) {
					context_param.setContextType(keyValue.substring(0, index),
							replaceEscapeChars(keyValue.substring(index + 1)));
				} else { // the subjob won't escape the especial chars
					context_param.setContextType(keyValue.substring(0, index), keyValue.substring(index + 1));
				}

			}

		} else if (arg.startsWith("--context_param")) {
			String keyValue = arg.substring(16);
			int index = -1;
			if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {
				if (fatherPid == null) {
					context_param.put(keyValue.substring(0, index), replaceEscapeChars(keyValue.substring(index + 1)));
				} else { // the subjob won't escape the especial chars
					context_param.put(keyValue.substring(0, index), keyValue.substring(index + 1));
				}
			}
		} else if (arg.startsWith("--context_file")) {
			String keyValue = arg.substring(15);
			String filePath = new String(java.util.Base64.getDecoder().decode(keyValue));
			java.nio.file.Path contextFile = java.nio.file.Paths.get(filePath);
			try (java.io.BufferedReader reader = java.nio.file.Files.newBufferedReader(contextFile)) {
				String line;
				while ((line = reader.readLine()) != null) {
					int index = -1;
					if ((index = line.indexOf('=')) > -1) {
						if (line.startsWith("--context_param")) {
							if ("id_Password".equals(context_param.getContextType(line.substring(16, index)))) {
								context_param.put(line.substring(16, index),
										routines.system.PasswordEncryptUtil.decryptPassword(line.substring(index + 1)));
							} else {
								context_param.put(line.substring(16, index), line.substring(index + 1));
							}
						} else {// --context_type
							context_param.setContextType(line.substring(15, index), line.substring(index + 1));
						}
					}
				}
			} catch (java.io.IOException e) {
				System.err.println("Could not load the context file: " + filePath);
				e.printStackTrace();
			}
		} else if (arg.startsWith("--log4jLevel=")) {
			log4jLevel = arg.substring(13);
		} else if (arg.startsWith("--audit.enabled") && arg.contains("=")) {// for trunjob call
			final int equal = arg.indexOf('=');
			final String key = arg.substring("--".length(), equal);
			System.setProperty(key, arg.substring(equal + 1));
		}
	}

	private static final String NULL_VALUE_EXPRESSION_IN_COMMAND_STRING_FOR_CHILD_JOB_ONLY = "<TALEND_NULL>";

	private final String[][] escapeChars = { { "\\\\", "\\" }, { "\\n", "\n" }, { "\\'", "\'" }, { "\\r", "\r" },
			{ "\\f", "\f" }, { "\\b", "\b" }, { "\\t", "\t" } };

	private String replaceEscapeChars(String keyValue) {

		if (keyValue == null || ("").equals(keyValue.trim())) {
			return keyValue;
		}

		StringBuilder result = new StringBuilder();
		int currIndex = 0;
		while (currIndex < keyValue.length()) {
			int index = -1;
			// judege if the left string includes escape chars
			for (String[] strArray : escapeChars) {
				index = keyValue.indexOf(strArray[0], currIndex);
				if (index >= 0) {

					result.append(keyValue.substring(currIndex, index + strArray[0].length()).replace(strArray[0],
							strArray[1]));
					currIndex = index + strArray[0].length();
					break;
				}
			}
			// if the left string doesn't include escape chars, append the left into the
			// result
			if (index < 0) {
				result.append(keyValue.substring(currIndex));
				currIndex = currIndex + keyValue.length();
			}
		}

		return result.toString();
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public String getStatus() {
		return status;
	}

	ResumeUtil resumeUtil = null;
}
/************************************************************************************************
 * 413333 characters generated by Qlik Talend Cloud Enterprise Edition on the 17
 * de março de 2026 01:47:40 BRT
 ************************************************************************************************/