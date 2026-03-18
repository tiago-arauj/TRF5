
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

	public void processos_1_tS3Connection_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		processos_1_tS3Connection_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void processos_1_tS3Get_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		processos_1_tS3Get_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void processos_1_tFileInputParquet_2_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		processos_1_tFileInputParquet_2_onSubJobError(exception, errorComponent, globalMap);
	}

	public void processos_1_tMap_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		processos_1_tFileInputParquet_2_onSubJobError(exception, errorComponent, globalMap);
	}

	public void processos_1_tDBOutput_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		processos_1_tFileInputParquet_2_onSubJobError(exception, errorComponent, globalMap);
	}

	public void processos_1_tDBRow_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		processos_1_tDBRow_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void processos_1_tDBInput_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		processos_1_tDBInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void processos_1_tFileOutputParquet_2_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		processos_1_tDBInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void processos_1_tS3Put_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		processos_1_tS3Put_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void talendJobLog_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		talendJobLog_onSubJobError(exception, errorComponent, globalMap);
	}

	public void processos_1_tS3Connection_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void processos_1_tS3Get_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void processos_1_tFileInputParquet_2_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void processos_1_tDBRow_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void processos_1_tDBInput_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void processos_1_tS3Put_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void talendJobLog_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void processos_1_tS3Connection_1Process(final java.util.Map<String, Object> globalMap)
			throws TalendException {
		globalMap.put("processos_1_tS3Connection_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("processos_1_tS3Connection_1", "thHMQJ_");

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
				 * [processos_1_tS3Connection_1 begin ] start
				 */

				sh("processos_1_tS3Connection_1");

				s(currentComponent = "processos_1_tS3Connection_1");

				int tos_count_processos_1_tS3Connection_1 = 0;

				if (log.isDebugEnabled())
					log.debug("processos_1_tS3Connection_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_processos_1_tS3Connection_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_processos_1_tS3Connection_1 = new StringBuilder();
							log4jParamters_processos_1_tS3Connection_1.append("Parameters:");
							log4jParamters_processos_1_tS3Connection_1
									.append("configuration.credentialProvider" + " = " + "STATIC_CREDENTIALS");
							log4jParamters_processos_1_tS3Connection_1.append(" | ");
							log4jParamters_processos_1_tS3Connection_1
									.append("configuration.staticCredentialConfiguration.accessKey" + " = "
											+ "AKIAUWW4VRZLOLIPY4ZH");
							log4jParamters_processos_1_tS3Connection_1.append(" | ");
							log4jParamters_processos_1_tS3Connection_1.append(
									"configuration.staticCredentialConfiguration.secretKey" + " = " + String.valueOf(
											"enc:routine.encryption.key.v1:+paSmIbv2ZGCkMqybgyCUPVOgua+wRV1+tL5DMJRM6mecJ8+Li06AJyLqp5BSRwbrps6x64/PNe31L1jHFZJOsc0cdE=")
											.substring(0, 4) + "...");
							log4jParamters_processos_1_tS3Connection_1.append(" | ");
							log4jParamters_processos_1_tS3Connection_1
									.append("configuration.assumeRole" + " = " + "false");
							log4jParamters_processos_1_tS3Connection_1.append(" | ");
							log4jParamters_processos_1_tS3Connection_1
									.append("configuration.region" + " = " + "DEFAULT");
							log4jParamters_processos_1_tS3Connection_1.append(" | ");
							log4jParamters_processos_1_tS3Connection_1
									.append("configuration.clientSideEncrypt" + " = " + "false");
							log4jParamters_processos_1_tS3Connection_1.append(" | ");
							log4jParamters_processos_1_tS3Connection_1
									.append("configuration.useRegionEndpoint" + " = " + "false");
							log4jParamters_processos_1_tS3Connection_1.append(" | ");
							log4jParamters_processos_1_tS3Connection_1
									.append("configuration.configClient" + " = " + "false");
							log4jParamters_processos_1_tS3Connection_1.append(" | ");
							log4jParamters_processos_1_tS3Connection_1
									.append("configuration.checkAccessibility" + " = " + "true");
							log4jParamters_processos_1_tS3Connection_1.append(" | ");
							log4jParamters_processos_1_tS3Connection_1
									.append("configuration.checkMethod" + " = " + "BY_ACCOUNT_OWNER");
							log4jParamters_processos_1_tS3Connection_1.append(" | ");
							log4jParamters_processos_1_tS3Connection_1
									.append("configuration.enableAccelerate" + " = " + "false");
							log4jParamters_processos_1_tS3Connection_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("processos_1_tS3Connection_1 - "
										+ (log4jParamters_processos_1_tS3Connection_1));
						}
					}
					new BytesLimit65535_processos_1_tS3Connection_1().limitLog4jByte();
				}
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_processos_1_tS3Connection_1 {

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

							} catch (java.lang.Exception e_processos_1_tS3Connection_1) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("processos_1_tS3Connection_1", "tS3Connection",
							new ParameterUtil_processos_1_tS3Connection_1().getParameter());
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("processos_1_tS3Connection_1", "tS3Connection_1", "tS3Connection");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				final org.talend.sdk.component.runtime.manager.ComponentManager mgr_processos_1_tS3Connection_1 = org.talend.sdk.component.runtime.manager.ComponentManager
						.instance();
				mgr_processos_1_tS3Connection_1.autoDiscoverPluginsIfEmpty(false, true);

				final java.util.Map<String, String> configuration_processos_1_tS3Connection_1 = new java.util.HashMap<>();
				final java.util.Map<String, String> registry_metadata_processos_1_tS3Connection_1 = new java.util.HashMap<>();

				final class SettingHelper_processos_1_tS3Connection_1 {
					final java.util.Map<String, String> configuration;

					SettingHelper_processos_1_tS3Connection_1(final java.util.Map<String, String> configuration) {
						this.configuration = configuration;
					}

					void put(String key, String value) {
						if (value != null) {
							configuration.put(key, value);
						}
					}
				}

				final SettingHelper_processos_1_tS3Connection_1 s_processos_1_tS3Connection_1 = new SettingHelper_processos_1_tS3Connection_1(
						configuration_processos_1_tS3Connection_1);
				Object dv_processos_1_tS3Connection_1;
				java.net.URL mappings_url_processos_1_tS3Connection_1 = this.getClass().getResource("/xmlMappings");
				globalMap.put("processos_1_tS3Connection_1_MAPPINGS_URL", mappings_url_processos_1_tS3Connection_1);
				globalMap.putIfAbsent("TALEND_PRODUCT_VERSION", "8.0");
				globalMap.putIfAbsent("TALEND_AWS_TRACKER", "APN/1.0 Talend/8.0 Studio/8.0 (Qlik Talend Cloud)");

				s_processos_1_tS3Connection_1.put("configuration.credentialProvider", "STATIC_CREDENTIALS");

				s_processos_1_tS3Connection_1.put("configuration.staticCredentialConfiguration.accessKey",
						"AKIAUWW4VRZLOLIPY4ZH");
				s_processos_1_tS3Connection_1.put("configuration.staticCredentialConfiguration.secretKey",
						routines.system.PasswordEncryptUtil.decryptPassword(
								"enc:routine.encryption.key.v1:Fx2UFDVuJwgcksIgocEMwV3lGErmJTTy4QeCDjVXpeg06Xj18gCyATkCV2pD+KGoGBv8RrYJ8jGQEx3kdd079c9sbrc="));

				s_processos_1_tS3Connection_1.put("configuration.assumeRole", "false");

				s_processos_1_tS3Connection_1.put("configuration.region", "DEFAULT");

				s_processos_1_tS3Connection_1.put("configuration.clientSideEncrypt", "false");

				s_processos_1_tS3Connection_1.put("configuration.useRegionEndpoint", "false");

				s_processos_1_tS3Connection_1.put("configuration.configClient", "false");

				s_processos_1_tS3Connection_1.put("configuration.checkAccessibility", "true");

				s_processos_1_tS3Connection_1.put("configuration.checkMethod", "BY_ACCOUNT_OWNER");

				s_processos_1_tS3Connection_1.put("configuration.enableAccelerate", "false");

				s_processos_1_tS3Connection_1.put("configuration.__version", "-1");
				final class SchemaSettingHelper_processos_1_tS3Connection_1_1 {

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
				new SchemaSettingHelper_processos_1_tS3Connection_1_1().set(configuration_processos_1_tS3Connection_1);

				mgr_processos_1_tS3Connection_1.findPlugin("aws-s3")
						.orElseThrow(() -> new IllegalStateException("Can't find the plugin : aws-s3"))
						.get(org.talend.sdk.component.runtime.manager.ContainerComponentRegistry.class).getServices()
						.stream().forEach(serviceMeta_processos_1_tS3Connection_1 -> {
							serviceMeta_processos_1_tS3Connection_1.getActions().stream()
									.filter(actionMeta_processos_1_tS3Connection_1 -> "create_connection"
											.equals(actionMeta_processos_1_tS3Connection_1.getType()))
									.forEach(actionMeta_processos_1_tS3Connection_1 -> {
										synchronized (serviceMeta_processos_1_tS3Connection_1.getInstance()) {
											org.talend.sdk.component.runtime.di.studio.RuntimeContextInjector
													.injectService(mgr_processos_1_tS3Connection_1, "aws-s3",
															new org.talend.sdk.component.api.context.RuntimeContextHolder(
																	"processos_1_tS3Connection_1", globalMap));

											Object connnection_processos_1_tS3Connection_1 = actionMeta_processos_1_tS3Connection_1
													.getInvoker().apply(configuration_processos_1_tS3Connection_1);

											globalMap.put("conn_processos_1_tS3Connection_1",
													connnection_processos_1_tS3Connection_1);

											try {
												configuration_processos_1_tS3Connection_1.put(
														"configuration.staticCredentialConfiguration.secretKey",
														routines.system.PasswordEncryptUtil.encryptPassword(
																configuration_processos_1_tS3Connection_1.get(
																		"configuration.staticCredentialConfiguration.secretKey")));
											} catch (Exception e) {
												e.printStackTrace();
											}
											globalMap.put("configuration_processos_1_tS3Connection_1",
													configuration_processos_1_tS3Connection_1);
										}
									});
						});

				/**
				 * [processos_1_tS3Connection_1 begin ] stop
				 */

				/**
				 * [processos_1_tS3Connection_1 main ] start
				 */

				s(currentComponent = "processos_1_tS3Connection_1");

				tos_count_processos_1_tS3Connection_1++;

				/**
				 * [processos_1_tS3Connection_1 main ] stop
				 */

				/**
				 * [processos_1_tS3Connection_1 process_data_begin ] start
				 */

				s(currentComponent = "processos_1_tS3Connection_1");

				/**
				 * [processos_1_tS3Connection_1 process_data_begin ] stop
				 */

				/**
				 * [processos_1_tS3Connection_1 process_data_end ] start
				 */

				s(currentComponent = "processos_1_tS3Connection_1");

				/**
				 * [processos_1_tS3Connection_1 process_data_end ] stop
				 */

				/**
				 * [processos_1_tS3Connection_1 end ] start
				 */

				s(currentComponent = "processos_1_tS3Connection_1");

				if (log.isDebugEnabled())
					log.debug("processos_1_tS3Connection_1 - " + ("Done."));

				ok_Hash.put("processos_1_tS3Connection_1", true);
				end_Hash.put("processos_1_tS3Connection_1", System.currentTimeMillis());

				/**
				 * [processos_1_tS3Connection_1 end ] stop
				 */

			} // end the resume

			if (resumeEntryMethodName == null || globalResumeTicket) {
				resumeUtil.addLog("CHECKPOINT", "CONNECTION:SUBJOB_OK:processos_1_tS3Connection_1:OnSubjobOk1", "",
						Thread.currentThread().getId() + "", "", "", "", "", "");
			}

			if (execStat) {
				runStat.updateStatOnConnection("processos_1_OnSubjobOk1", 0, "ok");
			}

			processos_1_tS3Get_1Process(globalMap);

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
				 * [processos_1_tS3Connection_1 finally ] start
				 */

				s(currentComponent = "processos_1_tS3Connection_1");

				/**
				 * [processos_1_tS3Connection_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("processos_1_tS3Connection_1_SUBPROCESS_STATE", 1);
	}

	public void processos_1_tS3Get_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("processos_1_tS3Get_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("processos_1_tS3Get_1", "RLF7UK_");

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
				 * [processos_1_tS3Get_1 begin ] start
				 */

				sh("processos_1_tS3Get_1");

				s(currentComponent = "processos_1_tS3Get_1");

				int tos_count_processos_1_tS3Get_1 = 0;

				if (log.isDebugEnabled())
					log.debug("processos_1_tS3Get_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_processos_1_tS3Get_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_processos_1_tS3Get_1 = new StringBuilder();
							log4jParamters_processos_1_tS3Get_1.append("Parameters:");
							log4jParamters_processos_1_tS3Get_1
									.append("configuration.bucket" + " = " + "\"peta-demo-qlik\"");
							log4jParamters_processos_1_tS3Get_1.append(" | ");
							log4jParamters_processos_1_tS3Get_1
									.append("configuration.key" + " = " + "\"bronze/processo_bronze.parquet\"");
							log4jParamters_processos_1_tS3Get_1.append(" | ");
							log4jParamters_processos_1_tS3Get_1.append("configuration.file" + " = "
									+ "\"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/processo_bronze_temp.parquet\"");
							log4jParamters_processos_1_tS3Get_1.append(" | ");
							log4jParamters_processos_1_tS3Get_1.append("configuration.dieOnError" + " = " + "false");
							log4jParamters_processos_1_tS3Get_1.append(" | ");
							log4jParamters_processos_1_tS3Get_1.append("configuration.useSelect" + " = " + "false");
							log4jParamters_processos_1_tS3Get_1.append(" | ");
							log4jParamters_processos_1_tS3Get_1
									.append("configuration.useTempFilesForParallelDownload" + " = " + "false");
							log4jParamters_processos_1_tS3Get_1.append(" | ");
							log4jParamters_processos_1_tS3Get_1.append("USE_EXISTING_CONNECTION" + " = " + "true");
							log4jParamters_processos_1_tS3Get_1.append(" | ");
							log4jParamters_processos_1_tS3Get_1
									.append("CONNECTION" + " = " + "processos_1_tS3Connection_1");
							log4jParamters_processos_1_tS3Get_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("processos_1_tS3Get_1 - " + (log4jParamters_processos_1_tS3Get_1));
						}
					}
					new BytesLimit65535_processos_1_tS3Get_1().limitLog4jByte();
				}
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_processos_1_tS3Get_1 {

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

								component_parameters.put("configuration.key", "bronze/processo_bronze.parquet");

								component_parameters.put("configuration.file",
										"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/processo_bronze_temp.parquet");

								component_parameters.put("configuration.dieOnError", "false");

								component_parameters.put("configuration.useSelect", "false");

								component_parameters.put("configuration.useTempFilesForParallelDownload", "false");
								component_parameters.put("USE_EXISTING_CONNECTION", String.valueOf("true"));
								component_parameters.put("CONNECTION", String.valueOf("processos_1_tS3Connection_1"));

							} catch (java.lang.Exception e_processos_1_tS3Get_1) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("processos_1_tS3Get_1", "S3Get",
							new ParameterUtil_processos_1_tS3Get_1().getParameter());
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("processos_1_tS3Get_1", "tS3Get_1", "S3Get");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				final org.talend.sdk.component.runtime.manager.ComponentManager mgr_processos_1_tS3Get_1 = org.talend.sdk.component.runtime.manager.ComponentManager
						.instance();
				mgr_processos_1_tS3Get_1.autoDiscoverPluginsIfEmpty(false, true);

				final java.util.Map<String, String> configuration_processos_1_tS3Get_1 = new java.util.HashMap<>();
				final java.util.Map<String, String> registry_metadata_processos_1_tS3Get_1 = new java.util.HashMap<>();

				final class SettingHelper_processos_1_tS3Get_1 {
					final java.util.Map<String, String> configuration;

					SettingHelper_processos_1_tS3Get_1(final java.util.Map<String, String> configuration) {
						this.configuration = configuration;
					}

					void put(String key, String value) {
						if (value != null) {
							configuration.put(key, value);
						}
					}
				}

				final SettingHelper_processos_1_tS3Get_1 s_processos_1_tS3Get_1 = new SettingHelper_processos_1_tS3Get_1(
						configuration_processos_1_tS3Get_1);
				Object dv_processos_1_tS3Get_1;
				java.net.URL mappings_url_processos_1_tS3Get_1 = this.getClass().getResource("/xmlMappings");
				globalMap.put("processos_1_tS3Get_1_MAPPINGS_URL", mappings_url_processos_1_tS3Get_1);
				globalMap.putIfAbsent("TALEND_PRODUCT_VERSION", "8.0");
				globalMap.putIfAbsent("TALEND_AWS_TRACKER", "APN/1.0 Talend/8.0 Studio/8.0 (Qlik Talend Cloud)");

				s_processos_1_tS3Get_1.put("configuration.bucket", "peta-demo-qlik");

				s_processos_1_tS3Get_1.put("configuration.key", "bronze/processo_bronze.parquet");

				s_processos_1_tS3Get_1.put("configuration.file",
						"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/processo_bronze_temp.parquet");

				s_processos_1_tS3Get_1.put("configuration.dieOnError", "false");

				s_processos_1_tS3Get_1.put("configuration.useSelect", "false");

				s_processos_1_tS3Get_1.put("configuration.useTempFilesForParallelDownload", "false");

				s_processos_1_tS3Get_1.put("configuration.dataset.__version", "-1");

				s_processos_1_tS3Get_1.put("configuration.dataset.datastore.__version", "-1");
				final class SchemaSettingHelper_processos_1_tS3Get_1_1 {

					public void set(final java.util.Map<String, String> configuration) throws java.lang.Exception {
					}
				}
				new SchemaSettingHelper_processos_1_tS3Get_1_1().set(configuration_processos_1_tS3Get_1);
				final class SchemaSettingHelper_processos_1_tS3Get_1_2 {

					public void set(final java.util.Map<String, String> configuration) throws java.lang.Exception {
					}
				}
				new SchemaSettingHelper_processos_1_tS3Get_1_2().set(configuration_processos_1_tS3Get_1);
				final java.util.Map<String, String> config_from_connection_processos_1_tS3Get_1 = (java.util.Map<String, String>) globalMap
						.get("configuration_processos_1_tS3Connection_1");
				final String conn_param_prefix_processos_1_tS3Get_1 = "configuration.dataset.datastore";
				if (config_from_connection_processos_1_tS3Get_1 != null
						&& conn_param_prefix_processos_1_tS3Get_1 != null) {
					final String prefix_processos_1_tS3Get_1 = config_from_connection_processos_1_tS3Get_1.keySet()
							.stream()
							.filter(key_processos_1_tS3Get_1 -> key_processos_1_tS3Get_1.endsWith(".__version"))
							.findFirst().map(key_processos_1_tS3Get_1 -> key_processos_1_tS3Get_1.substring(0,
									key_processos_1_tS3Get_1.lastIndexOf(".__version")))
							.orElse(null);

					if (prefix_processos_1_tS3Get_1 != null) {
						config_from_connection_processos_1_tS3Get_1.entrySet().stream()
								.filter(entry_processos_1_tS3Get_1 -> entry_processos_1_tS3Get_1.getKey()
										.startsWith(prefix_processos_1_tS3Get_1))
								.forEach(entry_processos_1_tS3Get_1 -> {
									configuration_processos_1_tS3Get_1.put(
											entry_processos_1_tS3Get_1.getKey().replaceFirst(
													prefix_processos_1_tS3Get_1,
													conn_param_prefix_processos_1_tS3Get_1),
											entry_processos_1_tS3Get_1.getValue());
								});
					}
				}

				final org.talend.sdk.component.runtime.standalone.DriverRunner standalone_processos_1_tS3Get_1 = mgr_processos_1_tS3Get_1
						.findDriverRunner("S3", "Get", 1, configuration_processos_1_tS3Get_1)
						.orElseThrow(() -> new IllegalArgumentException("Can't find S3#Get"));

				org.talend.sdk.component.runtime.di.studio.RuntimeContextInjector.injectLifecycle(
						standalone_processos_1_tS3Get_1, new org.talend.sdk.component.api.context.RuntimeContextHolder(
								"processos_1_tS3Get_1", globalMap));

				try {
					java.lang.reflect.Field field_processos_1_tS3Get_1 = standalone_processos_1_tS3Get_1.getClass()
							.getSuperclass().getDeclaredField("delegate");
					if (!field_processos_1_tS3Get_1.isAccessible()) {
						field_processos_1_tS3Get_1.setAccessible(true);
					}
					Object v_processos_1_tS3Get_1 = field_processos_1_tS3Get_1.get(standalone_processos_1_tS3Get_1);
					Object con_processos_1_tS3Get_1 = globalMap.get("conn_processos_1_tS3Connection_1");
					if (con_processos_1_tS3Get_1 == null) {
						throw new RuntimeException("can't find the connection object");
					}

					Class<?> current_processos_1_tS3Get_1 = v_processos_1_tS3Get_1.getClass();
					while (current_processos_1_tS3Get_1 != null && current_processos_1_tS3Get_1 != Object.class) {
						java.util.stream.Stream.of(current_processos_1_tS3Get_1.getDeclaredFields())
								.filter(f_processos_1_tS3Get_1 -> f_processos_1_tS3Get_1.isAnnotationPresent(
										org.talend.sdk.component.api.service.connection.Connection.class))
								.forEach(f_processos_1_tS3Get_1 -> {
									if (!f_processos_1_tS3Get_1.isAccessible()) {
										f_processos_1_tS3Get_1.setAccessible(true);
									}
									try {
										f_processos_1_tS3Get_1.set(v_processos_1_tS3Get_1, con_processos_1_tS3Get_1);
									} catch (final IllegalAccessException e_processos_1_tS3Get_1) {
										throw new IllegalStateException(e_processos_1_tS3Get_1);
									}
								});
						current_processos_1_tS3Get_1 = current_processos_1_tS3Get_1.getSuperclass();
					}
				} catch (Exception e_processos_1_tS3Get_1) {
					throw e_processos_1_tS3Get_1;
				}

				standalone_processos_1_tS3Get_1.start();
				globalMap.put("standalone_processos_1_tS3Get_1", standalone_processos_1_tS3Get_1);

				standalone_processos_1_tS3Get_1.runAtDriver();
//Standalone begin stub

				/**
				 * [processos_1_tS3Get_1 begin ] stop
				 */

				/**
				 * [processos_1_tS3Get_1 main ] start
				 */

				s(currentComponent = "processos_1_tS3Get_1");

				tos_count_processos_1_tS3Get_1++;

				/**
				 * [processos_1_tS3Get_1 main ] stop
				 */

				/**
				 * [processos_1_tS3Get_1 process_data_begin ] start
				 */

				s(currentComponent = "processos_1_tS3Get_1");

				/**
				 * [processos_1_tS3Get_1 process_data_begin ] stop
				 */

				/**
				 * [processos_1_tS3Get_1 process_data_end ] start
				 */

				s(currentComponent = "processos_1_tS3Get_1");

				/**
				 * [processos_1_tS3Get_1 process_data_end ] stop
				 */

				/**
				 * [processos_1_tS3Get_1 end ] start
				 */

				s(currentComponent = "processos_1_tS3Get_1");

				if (standalone_processos_1_tS3Get_1 != null) {
					standalone_processos_1_tS3Get_1.stop();
				}

				globalMap.remove("standalone_processos_1_tS3Get_1");

				if (log.isDebugEnabled())
					log.debug("processos_1_tS3Get_1 - " + ("Done."));

				ok_Hash.put("processos_1_tS3Get_1", true);
				end_Hash.put("processos_1_tS3Get_1", System.currentTimeMillis());

				/**
				 * [processos_1_tS3Get_1 end ] stop
				 */

			} // end the resume

			if (resumeEntryMethodName == null || globalResumeTicket) {
				resumeUtil.addLog("CHECKPOINT", "CONNECTION:SUBJOB_OK:processos_1_tS3Get_1:OnSubjobOk", "",
						Thread.currentThread().getId() + "", "", "", "", "", "");
			}

			if (execStat) {
				runStat.updateStatOnConnection("processos_1_OnSubjobOk2", 0, "ok");
			}

			processos_1_tFileInputParquet_2Process(globalMap);

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
				 * [processos_1_tS3Get_1 finally ] start
				 */

				s(currentComponent = "processos_1_tS3Get_1");

				final org.talend.sdk.component.runtime.standalone.DriverRunner standalone_processos_1_tS3Get_1 = org.talend.sdk.component.runtime.standalone.DriverRunner.class
						.cast(globalMap.remove("standalone_processos_1_tS3Get_1"));
				try {
					if (standalone_processos_1_tS3Get_1 != null) {
						standalone_processos_1_tS3Get_1.stop();
					}
				} catch (final RuntimeException re) {
					throw new TalendException(re, currentComponent, cLabel, globalMap);
				}

				/**
				 * [processos_1_tS3Get_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("processos_1_tS3Get_1_SUBPROCESS_STATE", 1);
	}

	public static class processos_1_transformacaoStruct
			implements routines.system.IPersistableRow<processos_1_transformacaoStruct> {
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

			return "";

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

		public String numero_processo;

		public String getNumero_processo() {
			return this.numero_processo;
		}

		public Boolean numero_processoIsNullable() {
			return false;
		}

		public Boolean numero_processoIsKey() {
			return false;
		}

		public Integer numero_processoLength() {
			return 25;
		}

		public Integer numero_processoPrecision() {
			return 0;
		}

		public String numero_processoDefault() {

			return null;

		}

		public String numero_processoComment() {

			return "";

		}

		public String numero_processoPattern() {

			return "";

		}

		public String numero_processoOriginalDbColumnName() {

			return "numero_processo";

		}

		public String id_tribunal;

		public String getId_tribunal() {
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

			return null;

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

		public String id_classe;

		public String getId_classe() {
			return this.id_classe;
		}

		public Boolean id_classeIsNullable() {
			return false;
		}

		public Boolean id_classeIsKey() {
			return false;
		}

		public Integer id_classeLength() {
			return 100;
		}

		public Integer id_classePrecision() {
			return 0;
		}

		public String id_classeDefault() {

			return null;

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

		public String id_assunto;

		public String getId_assunto() {
			return this.id_assunto;
		}

		public Boolean id_assuntoIsNullable() {
			return false;
		}

		public Boolean id_assuntoIsKey() {
			return false;
		}

		public Integer id_assuntoLength() {
			return null;
		}

		public Integer id_assuntoPrecision() {
			return null;
		}

		public String id_assuntoDefault() {

			return null;

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

		public String id_magistrado;

		public String getId_magistrado() {
			return this.id_magistrado;
		}

		public Boolean id_magistradoIsNullable() {
			return false;
		}

		public Boolean id_magistradoIsKey() {
			return false;
		}

		public Integer id_magistradoLength() {
			return null;
		}

		public Integer id_magistradoPrecision() {
			return null;
		}

		public String id_magistradoDefault() {

			return null;

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

		public java.util.Date data_ajuizamento;

		public java.util.Date getData_ajuizamento() {
			return this.data_ajuizamento;
		}

		public Boolean data_ajuizamentoIsNullable() {
			return false;
		}

		public Boolean data_ajuizamentoIsKey() {
			return false;
		}

		public Integer data_ajuizamentoLength() {
			return 13;
		}

		public Integer data_ajuizamentoPrecision() {
			return 0;
		}

		public String data_ajuizamentoDefault() {

			return null;

		}

		public String data_ajuizamentoComment() {

			return "";

		}

		public String data_ajuizamentoPattern() {

			return "dd-MM-yyyy";

		}

		public String data_ajuizamentoOriginalDbColumnName() {

			return "data_ajuizamento";

		}

		public String tempo_processo_dias;

		public String getTempo_processo_dias() {
			return this.tempo_processo_dias;
		}

		public Boolean tempo_processo_diasIsNullable() {
			return false;
		}

		public Boolean tempo_processo_diasIsKey() {
			return false;
		}

		public Integer tempo_processo_diasLength() {
			return null;
		}

		public Integer tempo_processo_diasPrecision() {
			return null;
		}

		public String tempo_processo_diasDefault() {

			return null;

		}

		public String tempo_processo_diasComment() {

			return "";

		}

		public String tempo_processo_diasPattern() {

			return "";

		}

		public String tempo_processo_diasOriginalDbColumnName() {

			return "tempo_processo_dias";

		}

		public BigDecimal valor_causa;

		public BigDecimal getValor_causa() {
			return this.valor_causa;
		}

		public Boolean valor_causaIsNullable() {
			return false;
		}

		public Boolean valor_causaIsKey() {
			return false;
		}

		public Integer valor_causaLength() {
			return null;
		}

		public Integer valor_causaPrecision() {
			return null;
		}

		public String valor_causaDefault() {

			return null;

		}

		public String valor_causaComment() {

			return "";

		}

		public String valor_causaPattern() {

			return "";

		}

		public String valor_causaOriginalDbColumnName() {

			return "valor_causa";

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
			final processos_1_transformacaoStruct other = (processos_1_transformacaoStruct) obj;

			if (this.id_processo != other.id_processo)
				return false;

			return true;
		}

		public void copyDataTo(processos_1_transformacaoStruct other) {

			other.id_processo = this.id_processo;
			other.numero_processo = this.numero_processo;
			other.id_tribunal = this.id_tribunal;
			other.id_classe = this.id_classe;
			other.id_assunto = this.id_assunto;
			other.id_magistrado = this.id_magistrado;
			other.data_ajuizamento = this.data_ajuizamento;
			other.tempo_processo_dias = this.tempo_processo_dias;
			other.valor_causa = this.valor_causa;

		}

		public void copyKeysDataTo(processos_1_transformacaoStruct other) {

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

					this.numero_processo = readString(dis);

					this.id_tribunal = readString(dis);

					this.id_classe = readString(dis);

					this.id_assunto = readString(dis);

					this.id_magistrado = readString(dis);

					this.data_ajuizamento = readDate(dis);

					this.tempo_processo_dias = readString(dis);

					this.valor_causa = (BigDecimal) dis.readObject();

				} catch (IOException e) {
					throw new RuntimeException(e);

				} catch (ClassNotFoundException eCNFE) {
					throw new RuntimeException(eCNFE);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_DEMOTRF5_TRF5) {

				try {

					int length = 0;

					this.id_processo = dis.readInt();

					this.numero_processo = readString(dis);

					this.id_tribunal = readString(dis);

					this.id_classe = readString(dis);

					this.id_assunto = readString(dis);

					this.id_magistrado = readString(dis);

					this.data_ajuizamento = readDate(dis);

					this.tempo_processo_dias = readString(dis);

					this.valor_causa = (BigDecimal) dis.readObject();

				} catch (IOException e) {
					throw new RuntimeException(e);

				} catch (ClassNotFoundException eCNFE) {
					throw new RuntimeException(eCNFE);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// int

				dos.writeInt(this.id_processo);

				// String

				writeString(this.numero_processo, dos);

				// String

				writeString(this.id_tribunal, dos);

				// String

				writeString(this.id_classe, dos);

				// String

				writeString(this.id_assunto, dos);

				// String

				writeString(this.id_magistrado, dos);

				// java.util.Date

				writeDate(this.data_ajuizamento, dos);

				// String

				writeString(this.tempo_processo_dias, dos);

				// BigDecimal

				dos.writeObject(this.valor_causa);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// int

				dos.writeInt(this.id_processo);

				// String

				writeString(this.numero_processo, dos);

				// String

				writeString(this.id_tribunal, dos);

				// String

				writeString(this.id_classe, dos);

				// String

				writeString(this.id_assunto, dos);

				// String

				writeString(this.id_magistrado, dos);

				// java.util.Date

				writeDate(this.data_ajuizamento, dos);

				// String

				writeString(this.tempo_processo_dias, dos);

				// BigDecimal

				dos.clearInstanceCache();
				dos.writeObject(this.valor_causa);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("id_processo=" + String.valueOf(id_processo));
			sb.append(",numero_processo=" + numero_processo);
			sb.append(",id_tribunal=" + id_tribunal);
			sb.append(",id_classe=" + id_classe);
			sb.append(",id_assunto=" + id_assunto);
			sb.append(",id_magistrado=" + id_magistrado);
			sb.append(",data_ajuizamento=" + String.valueOf(data_ajuizamento));
			sb.append(",tempo_processo_dias=" + tempo_processo_dias);
			sb.append(",valor_causa=" + String.valueOf(valor_causa));
			sb.append("]");

			return sb.toString();
		}

		public String toLogString() {
			StringBuilder sb = new StringBuilder();

			sb.append(id_processo);

			sb.append("|");

			if (numero_processo == null) {
				sb.append("<null>");
			} else {
				sb.append(numero_processo);
			}

			sb.append("|");

			if (id_tribunal == null) {
				sb.append("<null>");
			} else {
				sb.append(id_tribunal);
			}

			sb.append("|");

			if (id_classe == null) {
				sb.append("<null>");
			} else {
				sb.append(id_classe);
			}

			sb.append("|");

			if (id_assunto == null) {
				sb.append("<null>");
			} else {
				sb.append(id_assunto);
			}

			sb.append("|");

			if (id_magistrado == null) {
				sb.append("<null>");
			} else {
				sb.append(id_magistrado);
			}

			sb.append("|");

			if (data_ajuizamento == null) {
				sb.append("<null>");
			} else {
				sb.append(data_ajuizamento);
			}

			sb.append("|");

			if (tempo_processo_dias == null) {
				sb.append("<null>");
			} else {
				sb.append(tempo_processo_dias);
			}

			sb.append("|");

			if (valor_causa == null) {
				sb.append("<null>");
			} else {
				sb.append(valor_causa);
			}

			sb.append("|");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(processos_1_transformacaoStruct other) {

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

	public static class processos_1_row1Struct implements routines.system.IPersistableRow<processos_1_row1Struct> {
		final static byte[] commonByteArrayLock_DEMOTRF5_TRF5 = new byte[0];
		static byte[] commonByteArray_DEMOTRF5_TRF5 = new byte[0];

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

		public String numero_processo;

		public String getNumero_processo() {
			return this.numero_processo;
		}

		public Boolean numero_processoIsNullable() {
			return false;
		}

		public Boolean numero_processoIsKey() {
			return false;
		}

		public Integer numero_processoLength() {
			return 25;
		}

		public Integer numero_processoPrecision() {
			return 0;
		}

		public String numero_processoDefault() {

			return null;

		}

		public String numero_processoComment() {

			return "";

		}

		public String numero_processoPattern() {

			return "";

		}

		public String numero_processoOriginalDbColumnName() {

			return "numero_processo";

		}

		public String id_tribunal;

		public String getId_tribunal() {
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

			return "'TRF5'::character varying'";

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

		public String id_classe;

		public String getId_classe() {
			return this.id_classe;
		}

		public Boolean id_classeIsNullable() {
			return false;
		}

		public Boolean id_classeIsKey() {
			return false;
		}

		public Integer id_classeLength() {
			return 100;
		}

		public Integer id_classePrecision() {
			return 0;
		}

		public String id_classeDefault() {

			return null;

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

		public String id_assunto;

		public String getId_assunto() {
			return this.id_assunto;
		}

		public Boolean id_assuntoIsNullable() {
			return false;
		}

		public Boolean id_assuntoIsKey() {
			return false;
		}

		public Integer id_assuntoLength() {
			return null;
		}

		public Integer id_assuntoPrecision() {
			return null;
		}

		public String id_assuntoDefault() {

			return null;

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

		public String id_magistrado;

		public String getId_magistrado() {
			return this.id_magistrado;
		}

		public Boolean id_magistradoIsNullable() {
			return false;
		}

		public Boolean id_magistradoIsKey() {
			return false;
		}

		public Integer id_magistradoLength() {
			return null;
		}

		public Integer id_magistradoPrecision() {
			return null;
		}

		public String id_magistradoDefault() {

			return null;

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

		public java.util.Date data_ajuizamento;

		public java.util.Date getData_ajuizamento() {
			return this.data_ajuizamento;
		}

		public Boolean data_ajuizamentoIsNullable() {
			return false;
		}

		public Boolean data_ajuizamentoIsKey() {
			return false;
		}

		public Integer data_ajuizamentoLength() {
			return 13;
		}

		public Integer data_ajuizamentoPrecision() {
			return 0;
		}

		public String data_ajuizamentoDefault() {

			return null;

		}

		public String data_ajuizamentoComment() {

			return "";

		}

		public String data_ajuizamentoPattern() {

			return "dd-MM-yyyy";

		}

		public String data_ajuizamentoOriginalDbColumnName() {

			return "data_ajuizamento";

		}

		public String tempo_processo_dias;

		public String getTempo_processo_dias() {
			return this.tempo_processo_dias;
		}

		public Boolean tempo_processo_diasIsNullable() {
			return false;
		}

		public Boolean tempo_processo_diasIsKey() {
			return false;
		}

		public Integer tempo_processo_diasLength() {
			return null;
		}

		public Integer tempo_processo_diasPrecision() {
			return null;
		}

		public String tempo_processo_diasDefault() {

			return null;

		}

		public String tempo_processo_diasComment() {

			return "";

		}

		public String tempo_processo_diasPattern() {

			return "";

		}

		public String tempo_processo_diasOriginalDbColumnName() {

			return "tempo_processo_dias";

		}

		public String valor_causa;

		public String getValor_causa() {
			return this.valor_causa;
		}

		public Boolean valor_causaIsNullable() {
			return false;
		}

		public Boolean valor_causaIsKey() {
			return false;
		}

		public Integer valor_causaLength() {
			return null;
		}

		public Integer valor_causaPrecision() {
			return null;
		}

		public String valor_causaDefault() {

			return null;

		}

		public String valor_causaComment() {

			return "";

		}

		public String valor_causaPattern() {

			return "";

		}

		public String valor_causaOriginalDbColumnName() {

			return "valor_causa";

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

					this.numero_processo = readString(dis);

					this.id_tribunal = readString(dis);

					this.id_classe = readString(dis);

					this.id_assunto = readString(dis);

					this.id_magistrado = readString(dis);

					this.data_ajuizamento = readDate(dis);

					this.tempo_processo_dias = readString(dis);

					this.valor_causa = readString(dis);

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

					this.numero_processo = readString(dis);

					this.id_tribunal = readString(dis);

					this.id_classe = readString(dis);

					this.id_assunto = readString(dis);

					this.id_magistrado = readString(dis);

					this.data_ajuizamento = readDate(dis);

					this.tempo_processo_dias = readString(dis);

					this.valor_causa = readString(dis);

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

				writeString(this.numero_processo, dos);

				// String

				writeString(this.id_tribunal, dos);

				// String

				writeString(this.id_classe, dos);

				// String

				writeString(this.id_assunto, dos);

				// String

				writeString(this.id_magistrado, dos);

				// java.util.Date

				writeDate(this.data_ajuizamento, dos);

				// String

				writeString(this.tempo_processo_dias, dos);

				// String

				writeString(this.valor_causa, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// int

				dos.writeInt(this.id_processo);

				// String

				writeString(this.numero_processo, dos);

				// String

				writeString(this.id_tribunal, dos);

				// String

				writeString(this.id_classe, dos);

				// String

				writeString(this.id_assunto, dos);

				// String

				writeString(this.id_magistrado, dos);

				// java.util.Date

				writeDate(this.data_ajuizamento, dos);

				// String

				writeString(this.tempo_processo_dias, dos);

				// String

				writeString(this.valor_causa, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("id_processo=" + String.valueOf(id_processo));
			sb.append(",numero_processo=" + numero_processo);
			sb.append(",id_tribunal=" + id_tribunal);
			sb.append(",id_classe=" + id_classe);
			sb.append(",id_assunto=" + id_assunto);
			sb.append(",id_magistrado=" + id_magistrado);
			sb.append(",data_ajuizamento=" + String.valueOf(data_ajuizamento));
			sb.append(",tempo_processo_dias=" + tempo_processo_dias);
			sb.append(",valor_causa=" + valor_causa);
			sb.append("]");

			return sb.toString();
		}

		public String toLogString() {
			StringBuilder sb = new StringBuilder();

			sb.append(id_processo);

			sb.append("|");

			if (numero_processo == null) {
				sb.append("<null>");
			} else {
				sb.append(numero_processo);
			}

			sb.append("|");

			if (id_tribunal == null) {
				sb.append("<null>");
			} else {
				sb.append(id_tribunal);
			}

			sb.append("|");

			if (id_classe == null) {
				sb.append("<null>");
			} else {
				sb.append(id_classe);
			}

			sb.append("|");

			if (id_assunto == null) {
				sb.append("<null>");
			} else {
				sb.append(id_assunto);
			}

			sb.append("|");

			if (id_magistrado == null) {
				sb.append("<null>");
			} else {
				sb.append(id_magistrado);
			}

			sb.append("|");

			if (data_ajuizamento == null) {
				sb.append("<null>");
			} else {
				sb.append(data_ajuizamento);
			}

			sb.append("|");

			if (tempo_processo_dias == null) {
				sb.append("<null>");
			} else {
				sb.append(tempo_processo_dias);
			}

			sb.append("|");

			if (valor_causa == null) {
				sb.append("<null>");
			} else {
				sb.append(valor_causa);
			}

			sb.append("|");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(processos_1_row1Struct other) {

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

	public void processos_1_tFileInputParquet_2Process(final java.util.Map<String, Object> globalMap)
			throws TalendException {
		globalMap.put("processos_1_tFileInputParquet_2_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("processos_1_tFileInputParquet_2", "IAUwgQ_");

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

				processos_1_row1Struct processos_1_row1 = new processos_1_row1Struct();
				processos_1_transformacaoStruct processos_1_transformacao = new processos_1_transformacaoStruct();

				/**
				 * [processos_1_tDBOutput_1 begin ] start
				 */

				sh("processos_1_tDBOutput_1");

				s(currentComponent = "processos_1_tDBOutput_1");

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0,
						"processos_1_transformacao");

				int tos_count_processos_1_tDBOutput_1 = 0;

				if (log.isDebugEnabled())
					log.debug("processos_1_tDBOutput_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_processos_1_tDBOutput_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_processos_1_tDBOutput_1 = new StringBuilder();
							log4jParamters_processos_1_tDBOutput_1.append("Parameters:");
							log4jParamters_processos_1_tDBOutput_1.append("USE_EXISTING_CONNECTION" + " = " + "false");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("DB_VERSION" + " = " + "V9_X");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("HOST" + " = "
									+ "\"peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com\"");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("PORT" + " = " + "\"5432\"");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("DBNAME" + " = " + "\"qlik_demo\"");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("SCHEMA_DB" + " = " + "\"public\"");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("USER" + " = " + "\"peta_qlik\"");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("PASS" + " = " + String.valueOf(
									"enc:routine.encryption.key.v1:evJAVGlCmedcTX/V+qxyXX7Wi60AgVtkksN0CEnw2tuuFziZ9A==")
									.substring(0, 4) + "...");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("TABLE" + " = " + "\"stg_processos\"");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1
									.append("TABLE_ACTION" + " = " + "CREATE_IF_NOT_EXISTS");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("DATA_ACTION" + " = " + "INSERT");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("USE_SPATIAL_OPTIONS" + " = " + "false");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("SPECIFY_DATASOURCE_ALIAS" + " = " + "false");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("DIE_ON_ERROR" + " = " + "false");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("PROPERTIES" + " = " + "\"\"");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("COMMIT_EVERY" + " = " + "10000");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("ADD_COLS" + " = " + "[]");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("USE_FIELD_OPTIONS" + " = " + "false");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("ENABLE_DEBUG_MODE" + " = " + "false");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("SUPPORT_NULL_WHERE" + " = " + "false");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1
									.append("CONVERT_COLUMN_TABLE_TO_LOWERCASE" + " = " + "false");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("USE_BATCH_SIZE" + " = " + "true");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1.append("BATCH_SIZE" + " = " + "10000");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							log4jParamters_processos_1_tDBOutput_1
									.append("UNIFIED_COMPONENTS" + " = " + "tPostgresqlOutput");
							log4jParamters_processos_1_tDBOutput_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("processos_1_tDBOutput_1 - " + (log4jParamters_processos_1_tDBOutput_1));
						}
					}
					new BytesLimit65535_processos_1_tDBOutput_1().limitLog4jByte();
				}
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_processos_1_tDBOutput_1 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter(
								final processos_1_transformacaoStruct processos_1_transformacao) throws Exception {
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
								component_parameters.put("TABLE", String.valueOf("stg_processos"));
								component_parameters.put("TABLE_ACTION", String.valueOf("CREATE_IF_NOT_EXISTS"));
								component_parameters.put("DATA_ACTION", String.valueOf("INSERT"));
								component_parameters.put("USE_SPATIAL_OPTIONS", String.valueOf("false"));
								component_parameters.put("SPECIFY_DATASOURCE_ALIAS", String.valueOf("false"));
								component_parameters.put("DIE_ON_ERROR", String.valueOf("false"));
								component_parameters.put("PROPERTIES", String.valueOf(""));
								component_parameters.put("COMMIT_EVERY", String.valueOf("10000"));
								component_parameters.put("ADD_COLS", String.valueOf("[]"));
								component_parameters.put("USE_FIELD_OPTIONS", String.valueOf("false"));
								component_parameters.put("ENABLE_DEBUG_MODE", String.valueOf("false"));
								component_parameters.put("SUPPORT_NULL_WHERE", String.valueOf("false"));
								component_parameters.put("CONVERT_COLUMN_TABLE_TO_LOWERCASE", String.valueOf("false"));
								component_parameters.put("USE_BATCH_SIZE", String.valueOf("true"));
								component_parameters.put("BATCH_SIZE", String.valueOf("10000"));
								component_parameters.put("UNIFIED_COMPONENTS", String.valueOf("tPostgresqlOutput"));

							} catch (java.lang.Exception e_processos_1_tDBOutput_1) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("processos_1_tDBOutput_1", "tPostgresqlOutput",
							new ParameterUtil_processos_1_tDBOutput_1().getParameter(processos_1_transformacao));
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("processos_1_tDBOutput_1", "tDBOutput_1", "tPostgresqlOutput");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				String dbschema_processos_1_tDBOutput_1 = null;
				dbschema_processos_1_tDBOutput_1 = "public";

				String tableName_processos_1_tDBOutput_1 = null;
				if (dbschema_processos_1_tDBOutput_1 == null || dbschema_processos_1_tDBOutput_1.trim().length() == 0) {
					tableName_processos_1_tDBOutput_1 = ("stg_processos");
				} else {
					tableName_processos_1_tDBOutput_1 = dbschema_processos_1_tDBOutput_1 + "\".\"" + ("stg_processos");
				}

				int nb_line_processos_1_tDBOutput_1 = 0;
				int nb_line_update_processos_1_tDBOutput_1 = 0;
				int nb_line_inserted_processos_1_tDBOutput_1 = 0;
				int nb_line_deleted_processos_1_tDBOutput_1 = 0;
				int nb_line_rejected_processos_1_tDBOutput_1 = 0;

				int deletedCount_processos_1_tDBOutput_1 = 0;
				int updatedCount_processos_1_tDBOutput_1 = 0;
				int insertedCount_processos_1_tDBOutput_1 = 0;
				int rowsToCommitCount_processos_1_tDBOutput_1 = 0;
				int rejectedCount_processos_1_tDBOutput_1 = 0;

				boolean whetherReject_processos_1_tDBOutput_1 = false;

				java.sql.Connection conn_processos_1_tDBOutput_1 = null;
				String dbUser_processos_1_tDBOutput_1 = null;

				if (log.isDebugEnabled())
					log.debug(
							"processos_1_tDBOutput_1 - " + ("Driver ClassName: ") + ("org.postgresql.Driver") + ("."));
				java.lang.Class.forName("org.postgresql.Driver");

				String url_processos_1_tDBOutput_1 = "jdbc:postgresql://"
						+ "peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com" + ":" + "5432" + "/"
						+ "qlik_demo";
				dbUser_processos_1_tDBOutput_1 = "peta_qlik";

				final String decryptedPassword_processos_1_tDBOutput_1 = java.util.Optional
						.ofNullable(routines.system.PasswordEncryptUtil.decryptPassword(
								"enc:routine.encryption.key.v1:7NacHlqcQ8xrLyQqMTOgS5wrKfJCeiqex8VU+7FNjedAyJk4Fw=="))
						.orElse("");

				String dbPwd_processos_1_tDBOutput_1 = decryptedPassword_processos_1_tDBOutput_1;

				if (log.isDebugEnabled())
					log.debug(
							"processos_1_tDBOutput_1 - " + ("Connection attempts to '") + (url_processos_1_tDBOutput_1)
									+ ("' with the username '") + (dbUser_processos_1_tDBOutput_1) + ("'."));
				conn_processos_1_tDBOutput_1 = java.sql.DriverManager.getConnection(url_processos_1_tDBOutput_1,
						dbUser_processos_1_tDBOutput_1, dbPwd_processos_1_tDBOutput_1);
				if (log.isDebugEnabled())
					log.debug("processos_1_tDBOutput_1 - " + ("Connection to '") + (url_processos_1_tDBOutput_1)
							+ ("' has succeeded."));

				resourceMap.put("conn_processos_1_tDBOutput_1", conn_processos_1_tDBOutput_1);
				conn_processos_1_tDBOutput_1.setAutoCommit(false);
				int commitEvery_processos_1_tDBOutput_1 = 10000;
				int commitCounter_processos_1_tDBOutput_1 = 0;
				if (log.isDebugEnabled())
					log.debug("processos_1_tDBOutput_1 - " + ("Connection is set auto commit to '")
							+ (conn_processos_1_tDBOutput_1.getAutoCommit()) + ("'."));

				int batchSize_processos_1_tDBOutput_1 = 10000;
				int batchSizeCounter_processos_1_tDBOutput_1 = 0;

				int count_processos_1_tDBOutput_1 = 0;
				java.sql.DatabaseMetaData dbMetaData_processos_1_tDBOutput_1 = conn_processos_1_tDBOutput_1
						.getMetaData();
				boolean whetherExist_processos_1_tDBOutput_1 = false;
				try (java.sql.ResultSet rsTable_processos_1_tDBOutput_1 = dbMetaData_processos_1_tDBOutput_1
						.getTables(null, null, null, new String[] { "TABLE" })) {
					String defaultSchema_processos_1_tDBOutput_1 = "public";
					if (dbschema_processos_1_tDBOutput_1 == null
							|| dbschema_processos_1_tDBOutput_1.trim().length() == 0) {
						try (java.sql.Statement stmtSchema_processos_1_tDBOutput_1 = conn_processos_1_tDBOutput_1
								.createStatement();
								java.sql.ResultSet rsSchema_processos_1_tDBOutput_1 = stmtSchema_processos_1_tDBOutput_1
										.executeQuery("select current_schema() ")) {
							while (rsSchema_processos_1_tDBOutput_1.next()) {
								defaultSchema_processos_1_tDBOutput_1 = rsSchema_processos_1_tDBOutput_1
										.getString("current_schema");
							}
						}
					}
					while (rsTable_processos_1_tDBOutput_1.next()) {
						String table_processos_1_tDBOutput_1 = rsTable_processos_1_tDBOutput_1.getString("TABLE_NAME");
						String schema_processos_1_tDBOutput_1 = rsTable_processos_1_tDBOutput_1
								.getString("TABLE_SCHEM");
						if (table_processos_1_tDBOutput_1.equals(("stg_processos"))
								&& (schema_processos_1_tDBOutput_1.equals(dbschema_processos_1_tDBOutput_1)
										|| ((dbschema_processos_1_tDBOutput_1 == null
												|| dbschema_processos_1_tDBOutput_1.trim().length() == 0)
												&& defaultSchema_processos_1_tDBOutput_1
														.equals(schema_processos_1_tDBOutput_1)))) {
							whetherExist_processos_1_tDBOutput_1 = true;
							break;
						}
					}
				}
				if (!whetherExist_processos_1_tDBOutput_1) {
					try (java.sql.Statement stmtCreate_processos_1_tDBOutput_1 = conn_processos_1_tDBOutput_1
							.createStatement()) {
						if (log.isDebugEnabled())
							log.debug("processos_1_tDBOutput_1 - " + ("Creating") + (" table '")
									+ ("\"" + tableName_processos_1_tDBOutput_1 + "\"") + ("'."));
						stmtCreate_processos_1_tDBOutput_1.execute("CREATE TABLE \"" + tableName_processos_1_tDBOutput_1
								+ "\"(\"id_processo\" SERIAL  not null ,\"numero_processo\" VARCHAR(25)   not null ,\"id_tribunal\" VARCHAR(10)   not null ,\"id_classe\" VARCHAR(100)   not null ,\"id_assunto\" VARCHAR  not null ,\"id_magistrado\" VARCHAR  not null ,\"data_ajuizamento\" DATE  not null ,\"tempo_processo_dias\" VARCHAR  not null ,\"valor_causa\" NUMERIC  not null ,primary key(\"id_processo\"))");
						if (log.isDebugEnabled())
							log.debug("processos_1_tDBOutput_1 - " + ("Create") + (" table '")
									+ ("\"" + tableName_processos_1_tDBOutput_1 + "\"") + ("' has succeeded."));
					}
				}
				java.lang.StringBuilder sb_processos_1_tDBOutput_1 = new java.lang.StringBuilder();
				sb_processos_1_tDBOutput_1.append("INSERT INTO \"").append(tableName_processos_1_tDBOutput_1).append(
						"\" (\"id_processo\",\"numero_processo\",\"id_tribunal\",\"id_classe\",\"id_assunto\",\"id_magistrado\",\"data_ajuizamento\",\"tempo_processo_dias\",\"valor_causa\") VALUES (?,?,?,?,?,?,?,?,?)");

				String insert_processos_1_tDBOutput_1 = sb_processos_1_tDBOutput_1.toString();

				if (log.isDebugEnabled())
					log.debug(
							"processos_1_tDBOutput_1 - " + ("Executing '") + (insert_processos_1_tDBOutput_1) + ("'."));

				java.sql.PreparedStatement pstmt_processos_1_tDBOutput_1 = conn_processos_1_tDBOutput_1
						.prepareStatement(insert_processos_1_tDBOutput_1);
				resourceMap.put("pstmt_processos_1_tDBOutput_1", pstmt_processos_1_tDBOutput_1);

				/**
				 * [processos_1_tDBOutput_1 begin ] stop
				 */

				/**
				 * [processos_1_tMap_1 begin ] start
				 */

				sh("processos_1_tMap_1");

				s(currentComponent = "processos_1_tMap_1");

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0, "processos_1_row1");

				int tos_count_processos_1_tMap_1 = 0;

				if (log.isDebugEnabled())
					log.debug("processos_1_tMap_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_processos_1_tMap_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_processos_1_tMap_1 = new StringBuilder();
							log4jParamters_processos_1_tMap_1.append("Parameters:");
							log4jParamters_processos_1_tMap_1.append("LINK_STYLE" + " = " + "AUTO");
							log4jParamters_processos_1_tMap_1.append(" | ");
							log4jParamters_processos_1_tMap_1.append("TEMPORARY_DATA_DIRECTORY" + " = " + "");
							log4jParamters_processos_1_tMap_1.append(" | ");
							log4jParamters_processos_1_tMap_1.append("ROWS_BUFFER_SIZE" + " = " + "2000000");
							log4jParamters_processos_1_tMap_1.append(" | ");
							log4jParamters_processos_1_tMap_1
									.append("CHANGE_HASH_AND_EQUALS_FOR_BIGDECIMAL" + " = " + "true");
							log4jParamters_processos_1_tMap_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("processos_1_tMap_1 - " + (log4jParamters_processos_1_tMap_1));
						}
					}
					new BytesLimit65535_processos_1_tMap_1().limitLog4jByte();
				}
				boolean init_processos_1_tMap_1_0 = true;
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_processos_1_tMap_1 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter(final processos_1_row1Struct processos_1_row1)
								throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("LINK_STYLE", String.valueOf("AUTO"));
								component_parameters.put("ROWS_BUFFER_SIZE", String.valueOf("2000000"));
								component_parameters.put("CHANGE_HASH_AND_EQUALS_FOR_BIGDECIMAL",
										String.valueOf("true"));
								component_parameters.put("mapperData", String.valueOf(
										"{\"inputTables\":[{\"name\":\"processos_1_row1\",\"metadataTableEntries\":[{\"name\":\"id_processo\",\"expression\":\"\",\"type\":\"id_Integer\",\"nullable\":false},{\"name\":\"numero_processo\",\"expression\":\"\",\"type\":\"id_String\",\"nullable\":false},{\"name\":\"id_tribunal\",\"expression\":\"\",\"type\":\"id_String\",\"nullable\":false},{\"name\":\"id_classe\",\"expression\":\"\",\"type\":\"id_String\",\"nullable\":false},{\"name\":\"id_assunto\",\"expression\":\"\",\"type\":\"id_String\",\"nullable\":false},{\"name\":\"id_magistrado\",\"expression\":\"\",\"type\":\"id_String\",\"nullable\":false},{\"name\":\"data_ajuizamento\",\"expression\":\"\",\"type\":\"id_Date\",\"nullable\":false},{\"name\":\"tempo_processo_dias\",\"expression\":\"\",\"type\":\"id_String\",\"nullable\":false},{\"name\":\"valor_causa\",\"expression\":\"\",\"type\":\"id_String\",\"nullable\":false}]}],\"outputTables\":[{\"name\":\"processos_1_transformacao\",\"metadataTableEntries\":[{\"name\":\"id_processo\",\"expression\":\"processos_1_row1.id_processo\",\"type\":\"id_Integer\",\"nullable\":false},{\"name\":\"numero_processo\",\"expression\":\"processos_1_row1.numero_processo\",\"type\":\"id_String\",\"nullable\":false},{\"name\":\"id_tribunal\",\"expression\":\"processos_1_row1.id_tribunal\",\"type\":\"id_String\",\"nullable\":false},{\"name\":\"id_classe\",\"expression\":\"processos_1_row1.id_classe\",\"type\":\"id_String\",\"nullable\":false},{\"name\":\"id_assunto\",\"expression\":\"processos_1_row1.id_assunto\",\"type\":\"id_String\",\"nullable\":false},{\"name\":\"id_magistrado\",\"expression\":\"processos_1_row1.id_magistrado\",\"type\":\"id_String\",\"nullable\":false},{\"name\":\"data_ajuizamento\",\"expression\":\"processos_1_row1.data_ajuizamento\",\"type\":\"id_Date\",\"nullable\":false},{\"name\":\"tempo_processo_dias\",\"expression\":\"processos_1_row1.tempo_processo_dias\",\"type\":\"id_String\",\"nullable\":false},{\"name\":\"valor_causa\",\"expression\":\"processos_1_row1.valor_causa == null || processos_1_row1.valor_causa.isEmpty() ? null : new BigDecimal(processos_1_row1.valor_causa)\",\"type\":\"id_BigDecimal\",\"nullable\":false}]}],\"varsTables\":[{\"name\":\"Var\",\"metadataTableEntries\":[]}]}"));

							} catch (java.lang.Exception e_processos_1_tMap_1) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("processos_1_tMap_1", "tMap",
							new ParameterUtil_processos_1_tMap_1().getParameter(processos_1_row1));
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("processos_1_tMap_1", "tMap_1", "tMap");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

// ###############################
// # Lookup's keys initialization
				int count_processos_1_row1_processos_1_tMap_1 = 0;

// ###############################        

// ###############################
// # Vars initialization
// ###############################

// ###############################
// # Outputs initialization
				int count_processos_1_transformacao_processos_1_tMap_1 = 0;

				processos_1_transformacaoStruct processos_1_transformacao_tmp = new processos_1_transformacaoStruct();
// ###############################

				/**
				 * [processos_1_tMap_1 begin ] stop
				 */

				/**
				 * [processos_1_tFileInputParquet_2 begin ] start
				 */

				sh("processos_1_tFileInputParquet_2");

				s(currentComponent = "processos_1_tFileInputParquet_2");

				int tos_count_processos_1_tFileInputParquet_2 = 0;

				if (log.isDebugEnabled())
					log.debug("processos_1_tFileInputParquet_2 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_processos_1_tFileInputParquet_2 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_processos_1_tFileInputParquet_2 = new StringBuilder();
							log4jParamters_processos_1_tFileInputParquet_2.append("Parameters:");
							log4jParamters_processos_1_tFileInputParquet_2.append("FILENAME" + " = "
									+ "\"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/processo_bronze_temp.parquet\"");
							log4jParamters_processos_1_tFileInputParquet_2.append(" | ");
							log4jParamters_processos_1_tFileInputParquet_2
									.append("USE_EXTERNAL_HADOOP_DEPS" + " = " + "false");
							log4jParamters_processos_1_tFileInputParquet_2.append(" | ");
							if (log.isDebugEnabled())
								log.debug("processos_1_tFileInputParquet_2 - "
										+ (log4jParamters_processos_1_tFileInputParquet_2));
						}
					}
					new BytesLimit65535_processos_1_tFileInputParquet_2().limitLog4jByte();
				}
				boolean init_processos_1_tFileInputParquet_2_0 = true;
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_processos_1_tFileInputParquet_2 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter() throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("FILENAME", String.valueOf(
										"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/processo_bronze_temp.parquet"));
								component_parameters.put("USE_EXTERNAL_HADOOP_DEPS", String.valueOf("false"));

							} catch (java.lang.Exception e_processos_1_tFileInputParquet_2) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("processos_1_tFileInputParquet_2", "tFileInputParquet",
							new ParameterUtil_processos_1_tFileInputParquet_2().getParameter());
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("processos_1_tFileInputParquet_2", "tFileInputParquet_2", "tFileInputParquet");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				int nb_line_processos_1_tFileInputParquet_2 = 0;
				String filePath_processos_1_tFileInputParquet_2 = "C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/processo_bronze_temp.parquet";
				globalMap.put("processos_1_tFileInputParquet_2_FILE_PATH", filePath_processos_1_tFileInputParquet_2);

				org.apache.parquet.hadoop.ParquetFileReader readFooter_processos_1_tFileInputParquet_2 = null;
				org.apache.hadoop.conf.Configuration config_processos_1_tFileInputParquet_2 = new org.apache.hadoop.conf.Configuration();
				config_processos_1_tFileInputParquet_2.set("fs.file.impl",
						org.apache.hadoop.fs.LocalFileSystem.class.getName());
				org.apache.hadoop.fs.Path parquetFilePath_processos_1_tFileInputParquet_2 = new org.apache.hadoop.fs.Path(
						filePath_processos_1_tFileInputParquet_2);
				readFooter_processos_1_tFileInputParquet_2 = org.apache.parquet.hadoop.ParquetFileReader
						.open(org.apache.parquet.hadoop.util.HadoopInputFile.fromPath(
								parquetFilePath_processos_1_tFileInputParquet_2,
								config_processos_1_tFileInputParquet_2));
				org.apache.parquet.schema.MessageType schema_processos_1_tFileInputParquet_2 = readFooter_processos_1_tFileInputParquet_2
						.getFileMetaData().getSchema();
				org.apache.parquet.column.page.PageReadStore pageReadStore_processos_1_tFileInputParquet_2 = null;
				try {

					log.debug("processos_1_tFileInputParquet_2 - Retrieving records from the datasource.");

					while (null != (pageReadStore_processos_1_tFileInputParquet_2 = readFooter_processos_1_tFileInputParquet_2
							.readNextRowGroup())) {
						final long rows_processos_1_tFileInputParquet_2 = pageReadStore_processos_1_tFileInputParquet_2
								.getRowCount();
						final org.apache.parquet.io.MessageColumnIO columnIO_processos_1_tFileInputParquet_2 = new org.apache.parquet.io.ColumnIOFactory()
								.getColumnIO(schema_processos_1_tFileInputParquet_2);
						final org.apache.parquet.io.RecordReader<org.talend.parquet.data.Group> recordReader_processos_1_tFileInputParquet_2 = columnIO_processos_1_tFileInputParquet_2
								.getRecordReader(pageReadStore_processos_1_tFileInputParquet_2,
										new org.talend.parquet.data.simple.convert.GroupRecordConverter(
												schema_processos_1_tFileInputParquet_2));
						for (int i_processos_1_tFileInputParquet_2 = 0; i_processos_1_tFileInputParquet_2 < rows_processos_1_tFileInputParquet_2; i_processos_1_tFileInputParquet_2++) {
							nb_line_processos_1_tFileInputParquet_2++;
							processos_1_row1 = new processos_1_row1Struct();
							log.debug("processos_1_tFileInputParquet_2 - Retrieving the record "
									+ (nb_line_processos_1_tFileInputParquet_2) + ".");

							final org.talend.parquet.data.Group group_processos_1_tFileInputParquet_2 = recordReader_processos_1_tFileInputParquet_2
									.read();
							final org.apache.parquet.schema.GroupType groupType_processos_1_tFileInputParquet_2 = group_processos_1_tFileInputParquet_2
									.getType();
							String valueString_processos_1_tFileInputParquet_2 = null;
							valueString_processos_1_tFileInputParquet_2 = group_processos_1_tFileInputParquet_2
									.getValueToString(
											schema_processos_1_tFileInputParquet_2.getFieldIndex("id_processo"), 0);
							if (valueString_processos_1_tFileInputParquet_2 != null) {
								processos_1_row1.id_processo = group_processos_1_tFileInputParquet_2
										.getInteger("id_processo", 0);
							}
							valueString_processos_1_tFileInputParquet_2 = group_processos_1_tFileInputParquet_2
									.getValueToString(
											schema_processos_1_tFileInputParquet_2.getFieldIndex("numero_processo"), 0);
							if (valueString_processos_1_tFileInputParquet_2 != null) {
								processos_1_row1.numero_processo = valueString_processos_1_tFileInputParquet_2;
							}
							valueString_processos_1_tFileInputParquet_2 = group_processos_1_tFileInputParquet_2
									.getValueToString(
											schema_processos_1_tFileInputParquet_2.getFieldIndex("id_tribunal"), 0);
							if (valueString_processos_1_tFileInputParquet_2 != null) {
								processos_1_row1.id_tribunal = valueString_processos_1_tFileInputParquet_2;
							}
							valueString_processos_1_tFileInputParquet_2 = group_processos_1_tFileInputParquet_2
									.getValueToString(schema_processos_1_tFileInputParquet_2.getFieldIndex("id_classe"),
											0);
							if (valueString_processos_1_tFileInputParquet_2 != null) {
								processos_1_row1.id_classe = valueString_processos_1_tFileInputParquet_2;
							}
							valueString_processos_1_tFileInputParquet_2 = group_processos_1_tFileInputParquet_2
									.getValueToString(
											schema_processos_1_tFileInputParquet_2.getFieldIndex("id_assunto"), 0);
							if (valueString_processos_1_tFileInputParquet_2 != null) {
								processos_1_row1.id_assunto = valueString_processos_1_tFileInputParquet_2;
							}
							valueString_processos_1_tFileInputParquet_2 = group_processos_1_tFileInputParquet_2
									.getValueToString(
											schema_processos_1_tFileInputParquet_2.getFieldIndex("id_magistrado"), 0);
							if (valueString_processos_1_tFileInputParquet_2 != null) {
								processos_1_row1.id_magistrado = valueString_processos_1_tFileInputParquet_2;
							}
							valueString_processos_1_tFileInputParquet_2 = group_processos_1_tFileInputParquet_2
									.getValueToString(
											schema_processos_1_tFileInputParquet_2.getFieldIndex("data_ajuizamento"),
											0);
							if (valueString_processos_1_tFileInputParquet_2 != null) {
								if (org.apache.parquet.schema.OriginalType.DATE == groupType_processos_1_tFileInputParquet_2
										.getType("data_ajuizamento").getOriginalType()) {
									java.util.Calendar c_processos_1_tFileInputParquet_2 = new java.util.GregorianCalendar();
									c_processos_1_tFileInputParquet_2.setTime(new java.util.Date(0));
									c_processos_1_tFileInputParquet_2.add(java.util.Calendar.DAY_OF_YEAR,
											group_processos_1_tFileInputParquet_2.getInteger("data_ajuizamento", 0));
									processos_1_row1.data_ajuizamento = c_processos_1_tFileInputParquet_2.getTime();
								} else if (groupType_processos_1_tFileInputParquet_2.getType("data_ajuizamento")
										.isPrimitive()
										&& groupType_processos_1_tFileInputParquet_2.getType("data_ajuizamento")
												.asPrimitiveType()
												.getPrimitiveTypeName() == org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.INT96) {
									org.talend.parquet.data.simple.NanoTime nt_processos_1_tFileInputParquet_2 = org.talend.parquet.data.simple.NanoTime
											.fromBinary(group_processos_1_tFileInputParquet_2
													.getInt96("data_ajuizamento", 0));
									processos_1_row1.data_ajuizamento = org.talend.parquet.utils.NanoTimeUtils
											.getTimestamp(nt_processos_1_tFileInputParquet_2);
								} else if (org.apache.parquet.schema.OriginalType.TIMESTAMP_MILLIS == groupType_processos_1_tFileInputParquet_2
										.getType("data_ajuizamento").getOriginalType()
										&& groupType_processos_1_tFileInputParquet_2.getType("data_ajuizamento")
												.isPrimitive()
										&& groupType_processos_1_tFileInputParquet_2.getType("data_ajuizamento")
												.asPrimitiveType()
												.getPrimitiveTypeName() == org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.INT64) {
									long ts_processos_1_tFileInputParquet_2 = group_processos_1_tFileInputParquet_2
											.getLong("data_ajuizamento", 0);
									processos_1_row1.data_ajuizamento = new java.util.Date(
											ts_processos_1_tFileInputParquet_2);
								} else {
									processos_1_row1.data_ajuizamento = ParserUtils.parseTo_Date(
											group_processos_1_tFileInputParquet_2.getString("data_ajuizamento", 0),
											"dd-MM-yyyy");
								}
							}
							valueString_processos_1_tFileInputParquet_2 = group_processos_1_tFileInputParquet_2
									.getValueToString(
											schema_processos_1_tFileInputParquet_2.getFieldIndex("tempo_processo_dias"),
											0);
							if (valueString_processos_1_tFileInputParquet_2 != null) {
								processos_1_row1.tempo_processo_dias = valueString_processos_1_tFileInputParquet_2;
							}
							valueString_processos_1_tFileInputParquet_2 = group_processos_1_tFileInputParquet_2
									.getValueToString(
											schema_processos_1_tFileInputParquet_2.getFieldIndex("valor_causa"), 0);
							if (valueString_processos_1_tFileInputParquet_2 != null) {
								processos_1_row1.valor_causa = valueString_processos_1_tFileInputParquet_2;
							}

							/**
							 * [processos_1_tFileInputParquet_2 begin ] stop
							 */

							/**
							 * [processos_1_tFileInputParquet_2 main ] start
							 */

							s(currentComponent = "processos_1_tFileInputParquet_2");

							// QTUP-3575
							if (enableLineage && init_processos_1_tFileInputParquet_2_0) {
								class SchemaUtil_processos_1_row1 {

									private void a(java.util.List<java.util.Map<String, String>> schema,
											String... values) {
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
											final processos_1_row1Struct processos_1_row1) {
										java.util.List<java.util.Map<String, String>> s = new java.util.ArrayList<>();
										if (processos_1_row1 == null) {
											return s;
										}
										a(s, "id_processo", "id_processo", "true", "id_Integer", "SERIAL", "false", "",
												"10", "0");
										a(s, "numero_processo", "numero_processo", "false", "id_String", "VARCHAR",
												"false", "", "25", "0");
										a(s, "id_tribunal", "id_tribunal", "false", "id_String", "VARCHAR", "false", "",
												"10", "0");
										a(s, "id_classe", "id_classe", "false", "id_String", "VARCHAR", "false", "",
												"100", "0");
										a(s, "id_assunto", "id_assunto", "false", "id_String", "VARCHAR", "false", "",
												"null", "null");
										a(s, "id_magistrado", "id_magistrado", "false", "id_String", "VARCHAR", "false",
												"", "null", "null");
										a(s, "data_ajuizamento", "data_ajuizamento", "false", "id_Date", "DATE",
												"false", "dd-MM-yyyy", "13", "0");
										a(s, "tempo_processo_dias", "tempo_processo_dias", "false", "id_String",
												"VARCHAR", "false", "", "null", "null");
										a(s, "valor_causa", "valor_causa", "false", "id_String", "", "false", "",
												"null", "null");
										return s;
									}

								}

								if (processos_1_row1 != null) {
									talendJobLog.addConnectionSchemaMessage("processos_1_tFileInputParquet_2",
											"tFileInputParquet", "processos_1_tMap_1", "tMap",
											"processos_1_row1" + iterateId,
											new SchemaUtil_processos_1_row1().getSchema(processos_1_row1));
									talendJobLogProcess(globalMap);
									init_processos_1_tFileInputParquet_2_0 = false;
								}

							}
							// QTUP-3575

							tos_count_processos_1_tFileInputParquet_2++;

							/**
							 * [processos_1_tFileInputParquet_2 main ] stop
							 */

							/**
							 * [processos_1_tFileInputParquet_2 process_data_begin ] start
							 */

							s(currentComponent = "processos_1_tFileInputParquet_2");

							/**
							 * [processos_1_tFileInputParquet_2 process_data_begin ] stop
							 */

// Start of branch "processos_1_row1"
							if (processos_1_row1 != null) {

								/**
								 * [processos_1_tMap_1 main ] start
								 */

								s(currentComponent = "processos_1_tMap_1");

								if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

										, "processos_1_row1", "processos_1_tFileInputParquet_2", "tFileInputParquet_2",
										"tFileInputParquet", "processos_1_tMap_1", "tMap_1", "tMap"

								)) {
									talendJobLogProcess(globalMap);
								}

								if (log.isTraceEnabled()) {
									log.trace("processos_1_row1 - "
											+ (processos_1_row1 == null ? "" : processos_1_row1.toLogString()));
								}

								boolean hasCasePrimitiveKeyWithNull_processos_1_tMap_1 = false;

								// ###############################
								// # Input tables (lookups)

								boolean rejectedInnerJoin_processos_1_tMap_1 = false;
								boolean mainRowRejected_processos_1_tMap_1 = false;
								// ###############################
								{ // start of Var scope

									// ###############################
									// # Vars tables
									// ###############################
									// ###############################
									// # Output tables

									processos_1_transformacao = null;

// # Output table : 'processos_1_transformacao'
									count_processos_1_transformacao_processos_1_tMap_1++;

									processos_1_transformacao_tmp.id_processo = processos_1_row1.id_processo;
									processos_1_transformacao_tmp.numero_processo = processos_1_row1.numero_processo;
									processos_1_transformacao_tmp.id_tribunal = processos_1_row1.id_tribunal;
									processos_1_transformacao_tmp.id_classe = processos_1_row1.id_classe;
									processos_1_transformacao_tmp.id_assunto = processos_1_row1.id_assunto;
									processos_1_transformacao_tmp.id_magistrado = processos_1_row1.id_magistrado;
									processos_1_transformacao_tmp.data_ajuizamento = processos_1_row1.data_ajuizamento;
									processos_1_transformacao_tmp.tempo_processo_dias = processos_1_row1.tempo_processo_dias;
									processos_1_transformacao_tmp.valor_causa = processos_1_row1.valor_causa == null
											|| processos_1_row1.valor_causa.isEmpty() ? null
													: new BigDecimal(processos_1_row1.valor_causa);
									processos_1_transformacao = processos_1_transformacao_tmp;
									log.debug("processos_1_tMap_1 - Outputting the record "
											+ count_processos_1_transformacao_processos_1_tMap_1
											+ " of the output table 'processos_1_transformacao'.");

// ###############################

								} // end of Var scope

								rejectedInnerJoin_processos_1_tMap_1 = false;

								// QTUP-3575
								if (enableLineage && init_processos_1_tMap_1_0) {
									class SchemaUtil_processos_1_transformacao {

										private void a(java.util.List<java.util.Map<String, String>> schema,
												String... values) {
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
												final processos_1_transformacaoStruct processos_1_transformacao) {
											java.util.List<java.util.Map<String, String>> s = new java.util.ArrayList<>();
											if (processos_1_transformacao == null) {
												return s;
											}
											a(s, "id_processo", "id_processo", "true", "id_Integer", "SERIAL", "false",
													"", "10", "0");
											a(s, "numero_processo", "numero_processo", "false", "id_String", "VARCHAR",
													"false", "", "25", "0");
											a(s, "id_tribunal", "id_tribunal", "false", "id_String", "VARCHAR", "false",
													"", "10", "0");
											a(s, "id_classe", "id_classe", "false", "id_String", "VARCHAR", "false", "",
													"100", "0");
											a(s, "id_assunto", "id_assunto", "false", "id_String", "VARCHAR", "false",
													"", "null", "null");
											a(s, "id_magistrado", "id_magistrado", "false", "id_String", "VARCHAR",
													"false", "", "null", "null");
											a(s, "data_ajuizamento", "data_ajuizamento", "false", "id_Date", "DATE",
													"false", "dd-MM-yyyy", "13", "0");
											a(s, "tempo_processo_dias", "tempo_processo_dias", "false", "id_String",
													"VARCHAR", "false", "", "null", "null");
											a(s, "valor_causa", "valor_causa", "false", "id_BigDecimal", "", "false",
													"", "null", "null");
											return s;
										}

									}

									if (processos_1_transformacao != null) {
										talendJobLog.addConnectionSchemaMessage("processos_1_tMap_1", "tMap",
												"processos_1_tDBOutput_1", "tPostgresqlOutput",
												"processos_1_transformacao" + iterateId,
												new SchemaUtil_processos_1_transformacao()
														.getSchema(processos_1_transformacao));
										talendJobLogProcess(globalMap);
										init_processos_1_tMap_1_0 = false;
									}

								}
								// QTUP-3575

								tos_count_processos_1_tMap_1++;

								/**
								 * [processos_1_tMap_1 main ] stop
								 */

								/**
								 * [processos_1_tMap_1 process_data_begin ] start
								 */

								s(currentComponent = "processos_1_tMap_1");

								/**
								 * [processos_1_tMap_1 process_data_begin ] stop
								 */

// Start of branch "processos_1_transformacao"
								if (processos_1_transformacao != null) {

									/**
									 * [processos_1_tDBOutput_1 main ] start
									 */

									s(currentComponent = "processos_1_tDBOutput_1");

									if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

											, "processos_1_transformacao", "processos_1_tMap_1", "tMap_1", "tMap",
											"processos_1_tDBOutput_1", "tDBOutput_1", "tPostgresqlOutput"

									)) {
										talendJobLogProcess(globalMap);
									}

									if (log.isTraceEnabled()) {
										log.trace(
												"processos_1_transformacao - " + (processos_1_transformacao == null ? ""
														: processos_1_transformacao.toLogString()));
									}

									whetherReject_processos_1_tDBOutput_1 = false;
									pstmt_processos_1_tDBOutput_1.setInt(1, processos_1_transformacao.id_processo);

									if (processos_1_transformacao.numero_processo == null) {
										pstmt_processos_1_tDBOutput_1.setNull(2, java.sql.Types.VARCHAR);
									} else {
										pstmt_processos_1_tDBOutput_1.setString(2,
												processos_1_transformacao.numero_processo);
									}

									if (processos_1_transformacao.id_tribunal == null) {
										pstmt_processos_1_tDBOutput_1.setNull(3, java.sql.Types.VARCHAR);
									} else {
										pstmt_processos_1_tDBOutput_1.setString(3,
												processos_1_transformacao.id_tribunal);
									}

									if (processos_1_transformacao.id_classe == null) {
										pstmt_processos_1_tDBOutput_1.setNull(4, java.sql.Types.VARCHAR);
									} else {
										pstmt_processos_1_tDBOutput_1.setString(4, processos_1_transformacao.id_classe);
									}

									if (processos_1_transformacao.id_assunto == null) {
										pstmt_processos_1_tDBOutput_1.setNull(5, java.sql.Types.VARCHAR);
									} else {
										pstmt_processos_1_tDBOutput_1.setString(5,
												processos_1_transformacao.id_assunto);
									}

									if (processos_1_transformacao.id_magistrado == null) {
										pstmt_processos_1_tDBOutput_1.setNull(6, java.sql.Types.VARCHAR);
									} else {
										pstmt_processos_1_tDBOutput_1.setString(6,
												processos_1_transformacao.id_magistrado);
									}

									if (processos_1_transformacao.data_ajuizamento != null) {
										pstmt_processos_1_tDBOutput_1.setTimestamp(7, new java.sql.Timestamp(
												processos_1_transformacao.data_ajuizamento.getTime()));
									} else {
										pstmt_processos_1_tDBOutput_1.setNull(7, java.sql.Types.TIMESTAMP);
									}

									if (processos_1_transformacao.tempo_processo_dias == null) {
										pstmt_processos_1_tDBOutput_1.setNull(8, java.sql.Types.VARCHAR);
									} else {
										pstmt_processos_1_tDBOutput_1.setString(8,
												processos_1_transformacao.tempo_processo_dias);
									}

									pstmt_processos_1_tDBOutput_1.setBigDecimal(9,
											processos_1_transformacao.valor_causa);

									pstmt_processos_1_tDBOutput_1.addBatch();
									nb_line_processos_1_tDBOutput_1++;

									if (log.isDebugEnabled())
										log.debug("processos_1_tDBOutput_1 - " + ("Adding the record ")
												+ (nb_line_processos_1_tDBOutput_1) + (" to the ") + ("INSERT")
												+ (" batch."));
									batchSizeCounter_processos_1_tDBOutput_1++;

									if (!whetherReject_processos_1_tDBOutput_1) {
									}
									if ((batchSize_processos_1_tDBOutput_1 > 0)
											&& (batchSize_processos_1_tDBOutput_1 <= batchSizeCounter_processos_1_tDBOutput_1)) {
										try {
											int countSum_processos_1_tDBOutput_1 = 0;

											if (log.isDebugEnabled())
												log.debug("processos_1_tDBOutput_1 - " + ("Executing the ") + ("INSERT")
														+ (" batch."));
											for (int countEach_processos_1_tDBOutput_1 : pstmt_processos_1_tDBOutput_1
													.executeBatch()) {
												countSum_processos_1_tDBOutput_1 += (countEach_processos_1_tDBOutput_1 < 0
														? 0
														: countEach_processos_1_tDBOutput_1);
											}
											if (log.isDebugEnabled())
												log.debug("processos_1_tDBOutput_1 - " + ("The ") + ("INSERT")
														+ (" batch execution has succeeded."));
											rowsToCommitCount_processos_1_tDBOutput_1 += countSum_processos_1_tDBOutput_1;

											insertedCount_processos_1_tDBOutput_1 += countSum_processos_1_tDBOutput_1;

											batchSizeCounter_processos_1_tDBOutput_1 = 0;
										} catch (java.sql.BatchUpdateException e_processos_1_tDBOutput_1) {
											globalMap.put("processos_1_tDBOutput_1_ERROR_MESSAGE",
													e_processos_1_tDBOutput_1.getMessage());
											java.sql.SQLException ne_processos_1_tDBOutput_1 = e_processos_1_tDBOutput_1
													.getNextException(), sqle_processos_1_tDBOutput_1 = null;
											String errormessage_processos_1_tDBOutput_1;
											if (ne_processos_1_tDBOutput_1 != null) {
												// build new exception to provide the original cause
												sqle_processos_1_tDBOutput_1 = new java.sql.SQLException(
														e_processos_1_tDBOutput_1.getMessage() + "\ncaused by: "
																+ ne_processos_1_tDBOutput_1.getMessage(),
														ne_processos_1_tDBOutput_1.getSQLState(),
														ne_processos_1_tDBOutput_1.getErrorCode(),
														ne_processos_1_tDBOutput_1);
												errormessage_processos_1_tDBOutput_1 = sqle_processos_1_tDBOutput_1
														.getMessage();
											} else {
												errormessage_processos_1_tDBOutput_1 = e_processos_1_tDBOutput_1
														.getMessage();
											}

											int countSum_processos_1_tDBOutput_1 = 0;
											for (int countEach_processos_1_tDBOutput_1 : e_processos_1_tDBOutput_1
													.getUpdateCounts()) {
												countSum_processos_1_tDBOutput_1 += (countEach_processos_1_tDBOutput_1 < 0
														? 0
														: countEach_processos_1_tDBOutput_1);
											}
											rowsToCommitCount_processos_1_tDBOutput_1 += countSum_processos_1_tDBOutput_1;

											insertedCount_processos_1_tDBOutput_1 += countSum_processos_1_tDBOutput_1;

											log.error("processos_1_tDBOutput_1 - "
													+ (errormessage_processos_1_tDBOutput_1));
											System.err.println(errormessage_processos_1_tDBOutput_1);

										}
									}

									commitCounter_processos_1_tDBOutput_1++;
									if (commitEvery_processos_1_tDBOutput_1 <= commitCounter_processos_1_tDBOutput_1) {
										if ((batchSize_processos_1_tDBOutput_1 > 0)
												&& (batchSizeCounter_processos_1_tDBOutput_1 > 0)) {
											try {
												int countSum_processos_1_tDBOutput_1 = 0;

												if (log.isDebugEnabled())
													log.debug("processos_1_tDBOutput_1 - " + ("Executing the ")
															+ ("INSERT") + (" batch."));
												for (int countEach_processos_1_tDBOutput_1 : pstmt_processos_1_tDBOutput_1
														.executeBatch()) {
													countSum_processos_1_tDBOutput_1 += (countEach_processos_1_tDBOutput_1 < 0
															? 0
															: countEach_processos_1_tDBOutput_1);
												}
												if (log.isDebugEnabled())
													log.debug("processos_1_tDBOutput_1 - " + ("The ") + ("INSERT")
															+ (" batch execution has succeeded."));
												rowsToCommitCount_processos_1_tDBOutput_1 += countSum_processos_1_tDBOutput_1;

												insertedCount_processos_1_tDBOutput_1 += countSum_processos_1_tDBOutput_1;

												batchSizeCounter_processos_1_tDBOutput_1 = 0;
											} catch (java.sql.BatchUpdateException e_processos_1_tDBOutput_1) {
												globalMap.put("processos_1_tDBOutput_1_ERROR_MESSAGE",
														e_processos_1_tDBOutput_1.getMessage());
												java.sql.SQLException ne_processos_1_tDBOutput_1 = e_processos_1_tDBOutput_1
														.getNextException(), sqle_processos_1_tDBOutput_1 = null;
												String errormessage_processos_1_tDBOutput_1;
												if (ne_processos_1_tDBOutput_1 != null) {
													// build new exception to provide the original cause
													sqle_processos_1_tDBOutput_1 = new java.sql.SQLException(
															e_processos_1_tDBOutput_1.getMessage() + "\ncaused by: "
																	+ ne_processos_1_tDBOutput_1.getMessage(),
															ne_processos_1_tDBOutput_1.getSQLState(),
															ne_processos_1_tDBOutput_1.getErrorCode(),
															ne_processos_1_tDBOutput_1);
													errormessage_processos_1_tDBOutput_1 = sqle_processos_1_tDBOutput_1
															.getMessage();
												} else {
													errormessage_processos_1_tDBOutput_1 = e_processos_1_tDBOutput_1
															.getMessage();
												}

												int countSum_processos_1_tDBOutput_1 = 0;
												for (int countEach_processos_1_tDBOutput_1 : e_processos_1_tDBOutput_1
														.getUpdateCounts()) {
													countSum_processos_1_tDBOutput_1 += (countEach_processos_1_tDBOutput_1 < 0
															? 0
															: countEach_processos_1_tDBOutput_1);
												}
												rowsToCommitCount_processos_1_tDBOutput_1 += countSum_processos_1_tDBOutput_1;

												insertedCount_processos_1_tDBOutput_1 += countSum_processos_1_tDBOutput_1;

												log.error("processos_1_tDBOutput_1 - "
														+ (errormessage_processos_1_tDBOutput_1));
												System.err.println(errormessage_processos_1_tDBOutput_1);

											}
										}
										if (rowsToCommitCount_processos_1_tDBOutput_1 != 0) {

											if (log.isDebugEnabled())
												log.debug("processos_1_tDBOutput_1 - "
														+ ("Connection starting to commit ")
														+ (rowsToCommitCount_processos_1_tDBOutput_1)
														+ (" record(s)."));
										}
										conn_processos_1_tDBOutput_1.commit();
										if (rowsToCommitCount_processos_1_tDBOutput_1 != 0) {

											if (log.isDebugEnabled())
												log.debug("processos_1_tDBOutput_1 - "
														+ ("Connection commit has succeeded."));
											rowsToCommitCount_processos_1_tDBOutput_1 = 0;
										}
										commitCounter_processos_1_tDBOutput_1 = 0;
									}

									tos_count_processos_1_tDBOutput_1++;

									/**
									 * [processos_1_tDBOutput_1 main ] stop
									 */

									/**
									 * [processos_1_tDBOutput_1 process_data_begin ] start
									 */

									s(currentComponent = "processos_1_tDBOutput_1");

									/**
									 * [processos_1_tDBOutput_1 process_data_begin ] stop
									 */

									/**
									 * [processos_1_tDBOutput_1 process_data_end ] start
									 */

									s(currentComponent = "processos_1_tDBOutput_1");

									/**
									 * [processos_1_tDBOutput_1 process_data_end ] stop
									 */

								} // End of branch "processos_1_transformacao"

								/**
								 * [processos_1_tMap_1 process_data_end ] start
								 */

								s(currentComponent = "processos_1_tMap_1");

								/**
								 * [processos_1_tMap_1 process_data_end ] stop
								 */

							} // End of branch "processos_1_row1"

							/**
							 * [processos_1_tFileInputParquet_2 process_data_end ] start
							 */

							s(currentComponent = "processos_1_tFileInputParquet_2");

							/**
							 * [processos_1_tFileInputParquet_2 process_data_end ] stop
							 */

							/**
							 * [processos_1_tFileInputParquet_2 end ] start
							 */

							s(currentComponent = "processos_1_tFileInputParquet_2");

						}
					}
				} finally {
					if (readFooter_processos_1_tFileInputParquet_2 != null) {
						readFooter_processos_1_tFileInputParquet_2.close();
					}
				}
				globalMap.put("processos_1_tFileInputParquet_2_NB_LINE", nb_line_processos_1_tFileInputParquet_2);

				if (log.isDebugEnabled())
					log.debug("processos_1_tFileInputParquet_2 - " + ("Done."));

				ok_Hash.put("processos_1_tFileInputParquet_2", true);
				end_Hash.put("processos_1_tFileInputParquet_2", System.currentTimeMillis());

				/**
				 * [processos_1_tFileInputParquet_2 end ] stop
				 */

				/**
				 * [processos_1_tMap_1 end ] start
				 */

				s(currentComponent = "processos_1_tMap_1");

// ###############################
// # Lookup hashes releasing
// ###############################      
				log.debug("processos_1_tMap_1 - Written records count in the table 'processos_1_transformacao': "
						+ count_processos_1_transformacao_processos_1_tMap_1 + ".");

				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, "processos_1_row1", 2, 0,
						"processos_1_tFileInputParquet_2", "tFileInputParquet_2", "tFileInputParquet",
						"processos_1_tMap_1", "tMap_1", "tMap", "output")) {
					talendJobLogProcess(globalMap);
				}

				if (log.isDebugEnabled())
					log.debug("processos_1_tMap_1 - " + ("Done."));

				ok_Hash.put("processos_1_tMap_1", true);
				end_Hash.put("processos_1_tMap_1", System.currentTimeMillis());

				/**
				 * [processos_1_tMap_1 end ] stop
				 */

				/**
				 * [processos_1_tDBOutput_1 end ] start
				 */

				s(currentComponent = "processos_1_tDBOutput_1");

				try {
					int countSum_processos_1_tDBOutput_1 = 0;
					if (pstmt_processos_1_tDBOutput_1 != null && batchSizeCounter_processos_1_tDBOutput_1 > 0) {

						if (log.isDebugEnabled())
							log.debug("processos_1_tDBOutput_1 - " + ("Executing the ") + ("INSERT") + (" batch."));
						for (int countEach_processos_1_tDBOutput_1 : pstmt_processos_1_tDBOutput_1.executeBatch()) {
							countSum_processos_1_tDBOutput_1 += (countEach_processos_1_tDBOutput_1 < 0 ? 0
									: countEach_processos_1_tDBOutput_1);
						}
						rowsToCommitCount_processos_1_tDBOutput_1 += countSum_processos_1_tDBOutput_1;

						if (log.isDebugEnabled())
							log.debug("processos_1_tDBOutput_1 - " + ("The ") + ("INSERT")
									+ (" batch execution has succeeded."));
					}

					insertedCount_processos_1_tDBOutput_1 += countSum_processos_1_tDBOutput_1;

				} catch (java.sql.BatchUpdateException e_processos_1_tDBOutput_1) {
					globalMap.put("processos_1_tDBOutput_1_ERROR_MESSAGE", e_processos_1_tDBOutput_1.getMessage());
					java.sql.SQLException ne_processos_1_tDBOutput_1 = e_processos_1_tDBOutput_1.getNextException(),
							sqle_processos_1_tDBOutput_1 = null;
					String errormessage_processos_1_tDBOutput_1;
					if (ne_processos_1_tDBOutput_1 != null) {
						// build new exception to provide the original cause
						sqle_processos_1_tDBOutput_1 = new java.sql.SQLException(
								e_processos_1_tDBOutput_1.getMessage() + "\ncaused by: "
										+ ne_processos_1_tDBOutput_1.getMessage(),
								ne_processos_1_tDBOutput_1.getSQLState(), ne_processos_1_tDBOutput_1.getErrorCode(),
								ne_processos_1_tDBOutput_1);
						errormessage_processos_1_tDBOutput_1 = sqle_processos_1_tDBOutput_1.getMessage();
					} else {
						errormessage_processos_1_tDBOutput_1 = e_processos_1_tDBOutput_1.getMessage();
					}

					int countSum_processos_1_tDBOutput_1 = 0;
					for (int countEach_processos_1_tDBOutput_1 : e_processos_1_tDBOutput_1.getUpdateCounts()) {
						countSum_processos_1_tDBOutput_1 += (countEach_processos_1_tDBOutput_1 < 0 ? 0
								: countEach_processos_1_tDBOutput_1);
					}
					rowsToCommitCount_processos_1_tDBOutput_1 += countSum_processos_1_tDBOutput_1;

					insertedCount_processos_1_tDBOutput_1 += countSum_processos_1_tDBOutput_1;

					log.error("processos_1_tDBOutput_1 - " + (errormessage_processos_1_tDBOutput_1));
					System.err.println(errormessage_processos_1_tDBOutput_1);

				}

				if (pstmt_processos_1_tDBOutput_1 != null) {

					pstmt_processos_1_tDBOutput_1.close();
					resourceMap.remove("pstmt_processos_1_tDBOutput_1");
				}
				resourceMap.put("statementClosed_processos_1_tDBOutput_1", true);
				if (rowsToCommitCount_processos_1_tDBOutput_1 != 0) {

					if (log.isDebugEnabled())
						log.debug("processos_1_tDBOutput_1 - " + ("Connection starting to commit ")
								+ (rowsToCommitCount_processos_1_tDBOutput_1) + (" record(s)."));
				}
				conn_processos_1_tDBOutput_1.commit();
				if (rowsToCommitCount_processos_1_tDBOutput_1 != 0) {

					if (log.isDebugEnabled())
						log.debug("processos_1_tDBOutput_1 - " + ("Connection commit has succeeded."));
					rowsToCommitCount_processos_1_tDBOutput_1 = 0;
				}
				commitCounter_processos_1_tDBOutput_1 = 0;

				if (log.isDebugEnabled())
					log.debug("processos_1_tDBOutput_1 - " + ("Closing the connection to the database."));
				conn_processos_1_tDBOutput_1.close();

				if (log.isDebugEnabled())
					log.debug("processos_1_tDBOutput_1 - " + ("Connection to the database has closed."));
				resourceMap.put("finish_processos_1_tDBOutput_1", true);

				nb_line_deleted_processos_1_tDBOutput_1 = nb_line_deleted_processos_1_tDBOutput_1
						+ deletedCount_processos_1_tDBOutput_1;
				nb_line_update_processos_1_tDBOutput_1 = nb_line_update_processos_1_tDBOutput_1
						+ updatedCount_processos_1_tDBOutput_1;
				nb_line_inserted_processos_1_tDBOutput_1 = nb_line_inserted_processos_1_tDBOutput_1
						+ insertedCount_processos_1_tDBOutput_1;
				nb_line_rejected_processos_1_tDBOutput_1 = nb_line_rejected_processos_1_tDBOutput_1
						+ rejectedCount_processos_1_tDBOutput_1;

				globalMap.put("processos_1_tDBOutput_1_NB_LINE", nb_line_processos_1_tDBOutput_1);
				globalMap.put("processos_1_tDBOutput_1_NB_LINE_UPDATED", nb_line_update_processos_1_tDBOutput_1);
				globalMap.put("processos_1_tDBOutput_1_NB_LINE_INSERTED", nb_line_inserted_processos_1_tDBOutput_1);
				globalMap.put("processos_1_tDBOutput_1_NB_LINE_DELETED", nb_line_deleted_processos_1_tDBOutput_1);
				globalMap.put("processos_1_tDBOutput_1_NB_LINE_REJECTED", nb_line_rejected_processos_1_tDBOutput_1);

				if (log.isDebugEnabled())
					log.debug("processos_1_tDBOutput_1 - " + ("Has ") + ("inserted") + (" ")
							+ (nb_line_inserted_processos_1_tDBOutput_1) + (" record(s)."));

				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId,
						"processos_1_transformacao", 2, 0, "processos_1_tMap_1", "tMap_1", "tMap",
						"processos_1_tDBOutput_1", "tDBOutput_1", "tPostgresqlOutput", "output")) {
					talendJobLogProcess(globalMap);
				}

				if (log.isDebugEnabled())
					log.debug("processos_1_tDBOutput_1 - " + ("Done."));

				ok_Hash.put("processos_1_tDBOutput_1", true);
				end_Hash.put("processos_1_tDBOutput_1", System.currentTimeMillis());

				if (execStat) {
					runStat.updateStatOnConnection("processos_1_OnComponentOk1", 0, "ok");
				}
				processos_1_tDBRow_1Process(globalMap);

				/**
				 * [processos_1_tDBOutput_1 end ] stop
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
				 * [processos_1_tFileInputParquet_2 finally ] start
				 */

				s(currentComponent = "processos_1_tFileInputParquet_2");

				/**
				 * [processos_1_tFileInputParquet_2 finally ] stop
				 */

				/**
				 * [processos_1_tMap_1 finally ] start
				 */

				s(currentComponent = "processos_1_tMap_1");

				/**
				 * [processos_1_tMap_1 finally ] stop
				 */

				/**
				 * [processos_1_tDBOutput_1 finally ] start
				 */

				s(currentComponent = "processos_1_tDBOutput_1");

				try {
					if (resourceMap.get("statementClosed_processos_1_tDBOutput_1") == null) {
						java.sql.PreparedStatement pstmtToClose_processos_1_tDBOutput_1 = null;
						if ((pstmtToClose_processos_1_tDBOutput_1 = (java.sql.PreparedStatement) resourceMap
								.remove("pstmt_processos_1_tDBOutput_1")) != null) {
							pstmtToClose_processos_1_tDBOutput_1.close();
						}
					}
				} finally {
					if (resourceMap.get("finish_processos_1_tDBOutput_1") == null) {
						java.sql.Connection ctn_processos_1_tDBOutput_1 = null;
						if ((ctn_processos_1_tDBOutput_1 = (java.sql.Connection) resourceMap
								.get("conn_processos_1_tDBOutput_1")) != null) {
							try {
								if (log.isDebugEnabled())
									log.debug(
											"processos_1_tDBOutput_1 - " + ("Closing the connection to the database."));
								ctn_processos_1_tDBOutput_1.close();
								if (log.isDebugEnabled())
									log.debug(
											"processos_1_tDBOutput_1 - " + ("Connection to the database has closed."));
							} catch (java.sql.SQLException sqlEx_processos_1_tDBOutput_1) {
								String errorMessage_processos_1_tDBOutput_1 = "failed to close the connection in processos_1_tDBOutput_1 :"
										+ sqlEx_processos_1_tDBOutput_1.getMessage();
								log.error("processos_1_tDBOutput_1 - " + (errorMessage_processos_1_tDBOutput_1));
								System.err.println(errorMessage_processos_1_tDBOutput_1);
							}
						}
					}
				}

				/**
				 * [processos_1_tDBOutput_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("processos_1_tFileInputParquet_2_SUBPROCESS_STATE", 1);
	}

	public void processos_1_tDBRow_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("processos_1_tDBRow_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("processos_1_tDBRow_1", "Vr7ubR_");

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
				 * [processos_1_tDBRow_1 begin ] start
				 */

				sh("processos_1_tDBRow_1");

				s(currentComponent = "processos_1_tDBRow_1");

				int tos_count_processos_1_tDBRow_1 = 0;

				if (log.isDebugEnabled())
					log.debug("processos_1_tDBRow_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_processos_1_tDBRow_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_processos_1_tDBRow_1 = new StringBuilder();
							log4jParamters_processos_1_tDBRow_1.append("Parameters:");
							log4jParamters_processos_1_tDBRow_1.append("USE_EXISTING_CONNECTION" + " = " + "false");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							log4jParamters_processos_1_tDBRow_1.append("DB_VERSION" + " = " + "V9_X");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							log4jParamters_processos_1_tDBRow_1.append("HOST" + " = "
									+ "\"peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com\"");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							log4jParamters_processos_1_tDBRow_1.append("PORT" + " = " + "\"5432\"");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							log4jParamters_processos_1_tDBRow_1.append("DBNAME" + " = " + "\"qlik_demo\"");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							log4jParamters_processos_1_tDBRow_1.append("SCHEMA_DB" + " = " + "\"public\"");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							log4jParamters_processos_1_tDBRow_1.append("USER" + " = " + "\"peta_qlik\"");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							log4jParamters_processos_1_tDBRow_1.append("PASS" + " = " + String.valueOf(
									"enc:routine.encryption.key.v1:mNhlItzqvSjOHzQ/mceHp6qF2rHz1erOE/n5BxjVcRfnTIF06Q==")
									.substring(0, 4) + "...");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							log4jParamters_processos_1_tDBRow_1.append("QUERYSTORE" + " = " + "\"\"");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							log4jParamters_processos_1_tDBRow_1.append("QUERY" + " = "
									+ "\"DROP TABLE IF EXISTS silver_processos;   CREATE TABLE silver_processos AS   SELECT DISTINCT      f.id_processo,       f.numero_processo,       f.id_tribunal,       f.id_classe,       f.id_assunto,       f.id_magistrado,       f.data_ajuizamento,       CASE           WHEN f.valor_causa < 0 THEN 0           WHEN f.valor_causa > 100000000 THEN 100000000           ELSE f.valor_causa       END AS valor_causa,       COALESCE(f.tempo_processo_dias, '0') AS tempo_processo_dias   FROM stg_processos f   INNER JOIN stg_tribunal t ON CAST(f.id_tribunal AS VARCHAR) = CAST(t.id_tribunal AS VARCHAR)   INNER JOIN stg_magistrado m ON CAST(f.id_magistrado AS VARCHAR) = CAST(m.id_magistrado AS VARCHAR);\"");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							log4jParamters_processos_1_tDBRow_1.append("SPECIFY_DATASOURCE_ALIAS" + " = " + "false");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							log4jParamters_processos_1_tDBRow_1.append("DIE_ON_ERROR" + " = " + "false");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							log4jParamters_processos_1_tDBRow_1.append("PROPERTIES" + " = " + "\"\"");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							log4jParamters_processos_1_tDBRow_1.append("PROPAGATE_RECORD_SET" + " = " + "false");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							log4jParamters_processos_1_tDBRow_1.append("USE_PREPAREDSTATEMENT" + " = " + "false");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							log4jParamters_processos_1_tDBRow_1.append("COMMIT_EVERY" + " = " + "10000");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							log4jParamters_processos_1_tDBRow_1.append("UNIFIED_COMPONENTS" + " = " + "tPostgresqlRow");
							log4jParamters_processos_1_tDBRow_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("processos_1_tDBRow_1 - " + (log4jParamters_processos_1_tDBRow_1));
						}
					}
					new BytesLimit65535_processos_1_tDBRow_1().limitLog4jByte();
				}
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_processos_1_tDBRow_1 {

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
										"DROP TABLE IF EXISTS silver_processos; \nCREATE TABLE silver_processos AS \nSELECT DISTINCT\n    f.id_processo, \n    f"
												+ ".numero_processo, \n    f.id_tribunal, \n    f.id_classe, \n    f.id_assunto, \n    f.id_magistrado, \n    f.data_ajuiza"
												+ "mento, \n    CASE \n        WHEN f.valor_causa < 0 THEN 0 \n        WHEN f.valor_causa > 100000000 THEN 100000000 \n    "
												+ "    ELSE f.valor_causa \n    END AS valor_causa, \n    COALESCE(f.tempo_processo_dias, '0') AS tempo_processo_dias \nFRO"
												+ "M stg_processos f \nINNER JOIN stg_tribunal t ON CAST(f.id_tribunal AS VARCHAR) = CAST(t.id_tribunal AS VARCHAR) \nINNER"
												+ " JOIN stg_magistrado m ON CAST(f.id_magistrado AS VARCHAR) = CAST(m.id_magistrado AS VARCHAR);")
										.toString()));
								component_parameters.put("SPECIFY_DATASOURCE_ALIAS", String.valueOf("false"));
								component_parameters.put("DIE_ON_ERROR", String.valueOf("false"));
								component_parameters.put("PROPERTIES", String.valueOf(""));
								component_parameters.put("PROPAGATE_RECORD_SET", String.valueOf("false"));
								component_parameters.put("USE_PREPAREDSTATEMENT", String.valueOf("false"));
								component_parameters.put("COMMIT_EVERY", String.valueOf("10000"));
								component_parameters.put("UNIFIED_COMPONENTS", String.valueOf("tPostgresqlRow"));

							} catch (java.lang.Exception e_processos_1_tDBRow_1) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("processos_1_tDBRow_1", "tPostgresqlRow",
							new ParameterUtil_processos_1_tDBRow_1().getParameter());
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("processos_1_tDBRow_1", "tDBRow_1", "tPostgresqlRow");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				java.sql.Connection conn_processos_1_tDBRow_1 = null;
				String query_processos_1_tDBRow_1 = "";
				boolean whetherReject_processos_1_tDBRow_1 = false;
				int count_processos_1_tDBRow_1 = 0;
				String driverClass_processos_1_tDBRow_1 = "org.postgresql.Driver";
				java.lang.Class jdbcclazz_processos_1_tDBRow_1 = java.lang.Class
						.forName(driverClass_processos_1_tDBRow_1);

				String url_processos_1_tDBRow_1 = "jdbc:postgresql://"
						+ "peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com" + ":" + "5432" + "/"
						+ "qlik_demo";

				log.debug("processos_1_tDBRow_1 - Driver ClassName: " + driverClass_processos_1_tDBRow_1 + ".");

				String dbUser_processos_1_tDBRow_1 = "peta_qlik";

				final String decryptedPassword_processos_1_tDBRow_1 = java.util.Optional
						.ofNullable(routines.system.PasswordEncryptUtil.decryptPassword(
								"enc:routine.encryption.key.v1:lll3vxTqV6k7ZeTpH6JNChv3yGUgYjDnPclQ99/YrPWPd4UKSw=="))
						.orElse("");

				String dbPwd_processos_1_tDBRow_1 = decryptedPassword_processos_1_tDBRow_1;

				log.debug("processos_1_tDBRow_1 - Connection attempt to '"
						+ url_processos_1_tDBRow_1.replaceAll("(?<=trustStorePassword=)[^;]*", "********")
						+ "' with the username '" + dbUser_processos_1_tDBRow_1 + "'.");

				conn_processos_1_tDBRow_1 = java.sql.DriverManager.getConnection(url_processos_1_tDBRow_1,
						dbUser_processos_1_tDBRow_1, dbPwd_processos_1_tDBRow_1);

				log.debug("processos_1_tDBRow_1 - Connection to '"
						+ url_processos_1_tDBRow_1.replaceAll("(?<=trustStorePassword=)[^;]*", "********")
						+ "' has succeeded.");

				resourceMap.put("conn_processos_1_tDBRow_1", conn_processos_1_tDBRow_1);
				if (conn_processos_1_tDBRow_1.getAutoCommit()) {

					log.debug("processos_1_tDBRow_1 - Connection is set auto commit to 'false'.");

					conn_processos_1_tDBRow_1.setAutoCommit(false);

				}
				int commitEvery_processos_1_tDBRow_1 = 10000;
				int commitCounter_processos_1_tDBRow_1 = 0;

				java.sql.Statement stmt_processos_1_tDBRow_1 = conn_processos_1_tDBRow_1.createStatement();
				resourceMap.put("stmt_processos_1_tDBRow_1", stmt_processos_1_tDBRow_1);

				/**
				 * [processos_1_tDBRow_1 begin ] stop
				 */

				/**
				 * [processos_1_tDBRow_1 main ] start
				 */

				s(currentComponent = "processos_1_tDBRow_1");

				query_processos_1_tDBRow_1 = new StringBuilder().append(
						"DROP TABLE IF EXISTS silver_processos; \nCREATE TABLE silver_processos AS \nSELECT DISTINCT\n    f.id_processo, \n    f"
								+ ".numero_processo, \n    f.id_tribunal, \n    f.id_classe, \n    f.id_assunto, \n    f.id_magistrado, \n    f.data_ajuiza"
								+ "mento, \n    CASE \n        WHEN f.valor_causa < 0 THEN 0 \n        WHEN f.valor_causa > 100000000 THEN 100000000 \n    "
								+ "    ELSE f.valor_causa \n    END AS valor_causa, \n    COALESCE(f.tempo_processo_dias, '0') AS tempo_processo_dias \nFRO"
								+ "M stg_processos f \nINNER JOIN stg_tribunal t ON CAST(f.id_tribunal AS VARCHAR) = CAST(t.id_tribunal AS VARCHAR) \nINNER"
								+ " JOIN stg_magistrado m ON CAST(f.id_magistrado AS VARCHAR) = CAST(m.id_magistrado AS VARCHAR);")
						.toString();
				whetherReject_processos_1_tDBRow_1 = false;
				log.debug("processos_1_tDBRow_1 - Executing the query: '" + query_processos_1_tDBRow_1 + "'.");

				globalMap.put("processos_1_tDBRow_1_QUERY", query_processos_1_tDBRow_1);
				try {
					stmt_processos_1_tDBRow_1.execute(query_processos_1_tDBRow_1);
					log.debug("processos_1_tDBRow_1 - Execute the query: '" + query_processos_1_tDBRow_1
							+ "' has finished.");

				} catch (java.lang.Exception e) {
					whetherReject_processos_1_tDBRow_1 = true;

					log.error("processos_1_tDBRow_1 - " + e.getMessage());

					System.err.print(e.getMessage());
					globalMap.put("processos_1_tDBRow_1_ERROR_MESSAGE", e.getMessage());

				}

				if (!whetherReject_processos_1_tDBRow_1) {

				}

				commitCounter_processos_1_tDBRow_1++;
				if (commitEvery_processos_1_tDBRow_1 <= commitCounter_processos_1_tDBRow_1) {

					log.debug("processos_1_tDBRow_1 - Connection starting to commit.");

					conn_processos_1_tDBRow_1.commit();

					log.debug("processos_1_tDBRow_1 - Connection commit has succeeded.");

					commitCounter_processos_1_tDBRow_1 = 0;
				}

				tos_count_processos_1_tDBRow_1++;

				/**
				 * [processos_1_tDBRow_1 main ] stop
				 */

				/**
				 * [processos_1_tDBRow_1 process_data_begin ] start
				 */

				s(currentComponent = "processos_1_tDBRow_1");

				/**
				 * [processos_1_tDBRow_1 process_data_begin ] stop
				 */

				/**
				 * [processos_1_tDBRow_1 process_data_end ] start
				 */

				s(currentComponent = "processos_1_tDBRow_1");

				/**
				 * [processos_1_tDBRow_1 process_data_end ] stop
				 */

				/**
				 * [processos_1_tDBRow_1 end ] start
				 */

				s(currentComponent = "processos_1_tDBRow_1");

				globalMap.put("processos_1_tDBRow_1_NB_LINE", count_processos_1_tDBRow_1);
				stmt_processos_1_tDBRow_1.close();
				resourceMap.remove("stmt_processos_1_tDBRow_1");
				resourceMap.put("statementClosed_processos_1_tDBRow_1", true);
				if (commitEvery_processos_1_tDBRow_1 > commitCounter_processos_1_tDBRow_1) {

					log.debug("processos_1_tDBRow_1 - Connection starting to commit.");

					conn_processos_1_tDBRow_1.commit();

					log.debug("processos_1_tDBRow_1 - Connection commit has succeeded.");

					commitCounter_processos_1_tDBRow_1 = 0;

				}
				log.debug("processos_1_tDBRow_1 - Closing the connection to the database.");

				conn_processos_1_tDBRow_1.close();

				if ("com.mysql.cj.jdbc.Driver".equals((String) globalMap.get("driverClass_"))
						&& routines.system.BundleUtils.inOSGi()) {
					Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread").getMethod("checkedShutdown")
							.invoke(null, (Object[]) null);
				}

				log.debug("processos_1_tDBRow_1 - Connection to the database closed.");

				resourceMap.put("finish_processos_1_tDBRow_1", true);

				if (log.isDebugEnabled())
					log.debug("processos_1_tDBRow_1 - " + ("Done."));

				ok_Hash.put("processos_1_tDBRow_1", true);
				end_Hash.put("processos_1_tDBRow_1", System.currentTimeMillis());

				if (execStat) {
					runStat.updateStatOnConnection("processos_1_OnComponentOk2", 0, "ok");
				}
				processos_1_tDBInput_1Process(globalMap);

				/**
				 * [processos_1_tDBRow_1 end ] stop
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
				 * [processos_1_tDBRow_1 finally ] start
				 */

				s(currentComponent = "processos_1_tDBRow_1");

				try {
					if (resourceMap.get("statementClosed_processos_1_tDBRow_1") == null) {
						java.sql.Statement stmtToClose_processos_1_tDBRow_1 = null;
						if ((stmtToClose_processos_1_tDBRow_1 = (java.sql.Statement) resourceMap
								.remove("stmt_processos_1_tDBRow_1")) != null) {
							stmtToClose_processos_1_tDBRow_1.close();
						}
					}
				} finally {
					if (resourceMap.get("finish_processos_1_tDBRow_1") == null) {
						java.sql.Connection ctn_processos_1_tDBRow_1 = null;
						if ((ctn_processos_1_tDBRow_1 = (java.sql.Connection) resourceMap
								.get("conn_processos_1_tDBRow_1")) != null) {
							try {
								log.debug("processos_1_tDBRow_1 - Closing the connection to the database.");

								ctn_processos_1_tDBRow_1.close();
								log.debug("processos_1_tDBRow_1 - Connection to the database closed.");

							} catch (java.sql.SQLException sqlEx_processos_1_tDBRow_1) {
								String errorMessage_processos_1_tDBRow_1 = "failed to close the connection in processos_1_tDBRow_1 :"
										+ sqlEx_processos_1_tDBRow_1.getMessage();
								log.error("processos_1_tDBRow_1 - " + sqlEx_processos_1_tDBRow_1.getMessage());

								System.err.println(errorMessage_processos_1_tDBRow_1);
							}
						}
					}
				}

				/**
				 * [processos_1_tDBRow_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("processos_1_tDBRow_1_SUBPROCESS_STATE", 1);
	}

	public static class processos_1_row2Struct implements routines.system.IPersistableRow<processos_1_row2Struct> {
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
		public int compareTo(processos_1_row2Struct other) {

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

	public void processos_1_tDBInput_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("processos_1_tDBInput_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("processos_1_tDBInput_1", "QieCJH_");

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

				processos_1_row2Struct processos_1_row2 = new processos_1_row2Struct();

				/**
				 * [processos_1_tFileOutputParquet_2 begin ] start
				 */

				sh("processos_1_tFileOutputParquet_2");

				s(currentComponent = "processos_1_tFileOutputParquet_2");

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0, "processos_1_row2");

				int tos_count_processos_1_tFileOutputParquet_2 = 0;

				if (log.isDebugEnabled())
					log.debug("processos_1_tFileOutputParquet_2 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_processos_1_tFileOutputParquet_2 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_processos_1_tFileOutputParquet_2 = new StringBuilder();
							log4jParamters_processos_1_tFileOutputParquet_2.append("Parameters:");
							log4jParamters_processos_1_tFileOutputParquet_2.append("FILENAME" + " = "
									+ "\"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/processos_silver.parquet\"");
							log4jParamters_processos_1_tFileOutputParquet_2.append(" | ");
							log4jParamters_processos_1_tFileOutputParquet_2.append("FILE_ACTION" + " = " + "OVERWRITE");
							log4jParamters_processos_1_tFileOutputParquet_2.append(" | ");
							log4jParamters_processos_1_tFileOutputParquet_2
									.append("COMPRESSION" + " = " + "\"UNCOMPRESSED\"");
							log4jParamters_processos_1_tFileOutputParquet_2.append(" | ");
							log4jParamters_processos_1_tFileOutputParquet_2
									.append("USE_EXTERNAL_HADOOP_DEPS" + " = " + "false");
							log4jParamters_processos_1_tFileOutputParquet_2.append(" | ");
							log4jParamters_processos_1_tFileOutputParquet_2
									.append("ROW_GROUP_SIZE" + " = " + "134217728");
							log4jParamters_processos_1_tFileOutputParquet_2.append(" | ");
							log4jParamters_processos_1_tFileOutputParquet_2.append("PAGE_SIZE" + " = " + "1048576");
							log4jParamters_processos_1_tFileOutputParquet_2.append(" | ");
							if (log.isDebugEnabled())
								log.debug("processos_1_tFileOutputParquet_2 - "
										+ (log4jParamters_processos_1_tFileOutputParquet_2));
						}
					}
					new BytesLimit65535_processos_1_tFileOutputParquet_2().limitLog4jByte();
				}
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_processos_1_tFileOutputParquet_2 {

						private void a(java.util.List<java.util.Map<String, String>> schema, String... values) {
							java.util.Map<String, String> field = new java.util.HashMap<>();
							field.put("name", values[0]);
							field.put("talend_type", values[1]);
							schema.add(field);
						}

						public java.util.Map<String, String> getParameter(final processos_1_row2Struct processos_1_row2)
								throws Exception {
							java.util.Map<String, String> component_parameters = new java.util.HashMap<>();

							try {

								component_parameters.put("FILENAME", String.valueOf(
										"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/processos_silver.parquet"));
								component_parameters.put("FILE_ACTION", String.valueOf("OVERWRITE"));
								component_parameters.put("COMPRESSION", String.valueOf("UNCOMPRESSED"));
								component_parameters.put("USE_EXTERNAL_HADOOP_DEPS", String.valueOf("false"));
								component_parameters.put("ROW_GROUP_SIZE", String.valueOf("134217728"));
								component_parameters.put("PAGE_SIZE", String.valueOf("1048576"));

							} catch (java.lang.Exception e_processos_1_tFileOutputParquet_2) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("processos_1_tFileOutputParquet_2", "tFileOutputParquet",
							new ParameterUtil_processos_1_tFileOutputParquet_2().getParameter(processos_1_row2));
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("processos_1_tFileOutputParquet_2", "tFileOutputParquet_2",
							"tFileOutputParquet");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				int nb_line_processos_1_tFileOutputParquet_2 = 0;
				org.apache.parquet.schema.Types.MessageTypeBuilder schemaBuilder_processos_1_tFileOutputParquet_2 = org.apache.parquet.schema.Types
						.buildMessage();
				org.apache.parquet.schema.MessageType messageType_processos_1_tFileOutputParquet_2 = null;
				org.talend.parquet.data.simple.SimpleGroupFactory factory_processos_1_tFileOutputParquet_2 = null;
				org.apache.parquet.hadoop.ParquetWriter<org.talend.parquet.data.Group> writer_processos_1_tFileOutputParquet_2 = null;

				String filePath_processos_1_tFileOutputParquet_2 = "C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/processos_silver.parquet";
				globalMap.put("processos_1_tFileOutputParquet_2_FILE_PATH", filePath_processos_1_tFileOutputParquet_2);
				org.apache.hadoop.conf.Configuration config_processos_1_tFileOutputParquet_2 = new org.apache.hadoop.conf.Configuration();
				config_processos_1_tFileOutputParquet_2.set("fs.file.impl",
						org.apache.hadoop.fs.LocalFileSystem.class.getName());
				org.apache.hadoop.fs.Path path_processos_1_tFileOutputParquet_2 = new org.apache.hadoop.fs.Path(
						filePath_processos_1_tFileOutputParquet_2);
				// CRC file path
				String crcName_processos_1_tFileOutputParquet_2 = "." + path_processos_1_tFileOutputParquet_2.getName()
						+ ".crc";
				org.apache.hadoop.fs.Path crcPath_processos_1_tFileOutputParquet_2 = new org.apache.hadoop.fs.Path(
						path_processos_1_tFileOutputParquet_2.getParent(), crcName_processos_1_tFileOutputParquet_2);
				String compressName_processos_1_tFileOutputParquet_2 = "UNCOMPRESSED";
				int rowGroupSize_processos_1_tFileOutputParquet_2 = 134217728;
				int pageSize_processos_1_tFileOutputParquet_2 = 1048576;
				java.util.Map<String, org.talend.parquet.data.simple.SimpleGroupFactory> cachedFactory_processos_1_tFileOutputParquet_2 = new java.util.HashMap<>();
				schemaBuilder_processos_1_tFileOutputParquet_2.addField(org.talend.parquet.utils.TalendParquetUtils
						.createPrimitiveType("id_assunto", false, "INT32", "INT_32"));
				schemaBuilder_processos_1_tFileOutputParquet_2.addField(org.talend.parquet.utils.TalendParquetUtils
						.createPrimitiveType("cod_assunto_cnj", true, "BINARY", "UTF8"));
				schemaBuilder_processos_1_tFileOutputParquet_2.addField(org.talend.parquet.utils.TalendParquetUtils
						.createPrimitiveType("descricao_assunto", true, "BINARY", "UTF8"));
				schemaBuilder_processos_1_tFileOutputParquet_2.addField(org.talend.parquet.utils.TalendParquetUtils
						.createPrimitiveType("ramo_direito", true, "BINARY", "UTF8"));
				messageType_processos_1_tFileOutputParquet_2 = schemaBuilder_processos_1_tFileOutputParquet_2
						.named("Schema");

				org.talend.parquet.hadoop.TalendGroupWriteSupport.setSchema(
						messageType_processos_1_tFileOutputParquet_2, config_processos_1_tFileOutputParquet_2);
				factory_processos_1_tFileOutputParquet_2 = new org.talend.parquet.data.simple.SimpleGroupFactory(
						messageType_processos_1_tFileOutputParquet_2);
				org.apache.parquet.hadoop.ParquetWriter.Builder<org.talend.parquet.data.Group, org.talend.parquet.hadoop.TalendParquetWriter.Builder> builder_processos_1_tFileOutputParquet_2 = org.talend.parquet.hadoop.TalendParquetWriter
						.builder(org.apache.parquet.hadoop.util.HadoopOutputFile.fromPath(
								path_processos_1_tFileOutputParquet_2, config_processos_1_tFileOutputParquet_2));
				builder_processos_1_tFileOutputParquet_2
						.withWriteMode(org.apache.parquet.hadoop.ParquetFileWriter.Mode.OVERWRITE)
						.withCompressionCodec(org.apache.parquet.hadoop.metadata.CompressionCodecName
								.fromConf(compressName_processos_1_tFileOutputParquet_2))
						.withRowGroupSize(rowGroupSize_processos_1_tFileOutputParquet_2)
						.withPageSize(pageSize_processos_1_tFileOutputParquet_2)
						.withConf(config_processos_1_tFileOutputParquet_2);

				writer_processos_1_tFileOutputParquet_2 = builder_processos_1_tFileOutputParquet_2.build();

				/**
				 * [processos_1_tFileOutputParquet_2 begin ] stop
				 */

				/**
				 * [processos_1_tDBInput_1 begin ] start
				 */

				sh("processos_1_tDBInput_1");

				s(currentComponent = "processos_1_tDBInput_1");

				int tos_count_processos_1_tDBInput_1 = 0;

				if (log.isDebugEnabled())
					log.debug("processos_1_tDBInput_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_processos_1_tDBInput_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_processos_1_tDBInput_1 = new StringBuilder();
							log4jParamters_processos_1_tDBInput_1.append("Parameters:");
							log4jParamters_processos_1_tDBInput_1.append("USE_EXISTING_CONNECTION" + " = " + "false");
							log4jParamters_processos_1_tDBInput_1.append(" | ");
							log4jParamters_processos_1_tDBInput_1.append("DB_VERSION" + " = " + "V9_X");
							log4jParamters_processos_1_tDBInput_1.append(" | ");
							log4jParamters_processos_1_tDBInput_1.append("HOST" + " = "
									+ "\"peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com\"");
							log4jParamters_processos_1_tDBInput_1.append(" | ");
							log4jParamters_processos_1_tDBInput_1.append("PORT" + " = " + "\"5432\"");
							log4jParamters_processos_1_tDBInput_1.append(" | ");
							log4jParamters_processos_1_tDBInput_1.append("DBNAME" + " = " + "\"qlik_demo\"");
							log4jParamters_processos_1_tDBInput_1.append(" | ");
							log4jParamters_processos_1_tDBInput_1.append("SCHEMA_DB" + " = " + "\"public\"");
							log4jParamters_processos_1_tDBInput_1.append(" | ");
							log4jParamters_processos_1_tDBInput_1.append("USER" + " = " + "\"peta_qlik\"");
							log4jParamters_processos_1_tDBInput_1.append(" | ");
							log4jParamters_processos_1_tDBInput_1.append("PASS" + " = " + String.valueOf(
									"enc:routine.encryption.key.v1:zQPAcpzoIVXf2PQYmc8i6YvTieZHt6ercmSPq+DEzdvsLK85pg==")
									.substring(0, 4) + "...");
							log4jParamters_processos_1_tDBInput_1.append(" | ");
							log4jParamters_processos_1_tDBInput_1.append("QUERYSTORE" + " = " + "\"\"");
							log4jParamters_processos_1_tDBInput_1.append(" | ");
							log4jParamters_processos_1_tDBInput_1
									.append("QUERY" + " = " + "\"SELECT *  FROM silver_processos;\"");
							log4jParamters_processos_1_tDBInput_1.append(" | ");
							log4jParamters_processos_1_tDBInput_1.append("SPECIFY_DATASOURCE_ALIAS" + " = " + "false");
							log4jParamters_processos_1_tDBInput_1.append(" | ");
							log4jParamters_processos_1_tDBInput_1.append("PROPERTIES" + " = " + "\"\"");
							log4jParamters_processos_1_tDBInput_1.append(" | ");
							log4jParamters_processos_1_tDBInput_1.append("USE_CURSOR" + " = " + "false");
							log4jParamters_processos_1_tDBInput_1.append(" | ");
							log4jParamters_processos_1_tDBInput_1.append("TRIM_ALL_COLUMN" + " = " + "false");
							log4jParamters_processos_1_tDBInput_1.append(" | ");
							log4jParamters_processos_1_tDBInput_1.append(
									"TRIM_COLUMN" + " = " + "[{TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("id_assunto")
											+ "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("cod_assunto_cnj")
											+ "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("descricao_assunto")
											+ "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("ramo_direito") + "}]");
							log4jParamters_processos_1_tDBInput_1.append(" | ");
							log4jParamters_processos_1_tDBInput_1
									.append("UNIFIED_COMPONENTS" + " = " + "tPostgresqlInput");
							log4jParamters_processos_1_tDBInput_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("processos_1_tDBInput_1 - " + (log4jParamters_processos_1_tDBInput_1));
						}
					}
					new BytesLimit65535_processos_1_tDBInput_1().limitLog4jByte();
				}
				boolean init_processos_1_tDBInput_1_0 = true;
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_processos_1_tDBInput_1 {

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
								component_parameters.put("QUERY", String.valueOf("SELECT *\nFROM silver_processos;"));
								component_parameters.put("SPECIFY_DATASOURCE_ALIAS", String.valueOf("false"));
								component_parameters.put("PROPERTIES", String.valueOf(""));
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

							} catch (java.lang.Exception e_processos_1_tDBInput_1) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("processos_1_tDBInput_1", "tPostgresqlInput",
							new ParameterUtil_processos_1_tDBInput_1().getParameter());
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("processos_1_tDBInput_1", "tDBInput_1", "tPostgresqlInput");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				int nb_line_processos_1_tDBInput_1 = 0;
				java.sql.Connection conn_processos_1_tDBInput_1 = null;
				String driverClass_processos_1_tDBInput_1 = "org.postgresql.Driver";
				java.lang.Class jdbcclazz_processos_1_tDBInput_1 = java.lang.Class
						.forName(driverClass_processos_1_tDBInput_1);
				String dbUser_processos_1_tDBInput_1 = "peta_qlik";

				final String decryptedPassword_processos_1_tDBInput_1 = java.util.Optional
						.ofNullable(routines.system.PasswordEncryptUtil.decryptPassword(
								"enc:routine.encryption.key.v1:pC/oPc+ESmm2ZNFMEjChoHogMso9yA7Zx7sP4ezdvZCuQc6puQ=="))
						.orElse("");

				String dbPwd_processos_1_tDBInput_1 = decryptedPassword_processos_1_tDBInput_1;

				String url_processos_1_tDBInput_1 = "jdbc:postgresql://"
						+ "peta-demo-qlik-instance-1.cb64q4o4ssoa.us-east-2.rds.amazonaws.com" + ":" + "5432" + "/"
						+ "qlik_demo";

				log.debug("processos_1_tDBInput_1 - Driver ClassName: " + driverClass_processos_1_tDBInput_1 + ".");

				log.debug("processos_1_tDBInput_1 - Connection attempt to '"
						+ url_processos_1_tDBInput_1.replaceAll("(?<=trustStorePassword=)[^;]*", "********")
						+ "' with the username '" + dbUser_processos_1_tDBInput_1 + "'.");

				conn_processos_1_tDBInput_1 = java.sql.DriverManager.getConnection(url_processos_1_tDBInput_1,
						dbUser_processos_1_tDBInput_1, dbPwd_processos_1_tDBInput_1);
				log.debug("processos_1_tDBInput_1 - Connection to '"
						+ url_processos_1_tDBInput_1.replaceAll("(?<=trustStorePassword=)[^;]*", "********")
						+ "' has succeeded.");

				log.debug("processos_1_tDBInput_1 - Connection is set auto commit to 'false'.");

				conn_processos_1_tDBInput_1.setAutoCommit(false);

				java.sql.Statement stmt_processos_1_tDBInput_1 = conn_processos_1_tDBInput_1.createStatement();

				String dbquery_processos_1_tDBInput_1 = "SELECT *\nFROM silver_processos;";

				log.debug("processos_1_tDBInput_1 - Executing the query: '" + dbquery_processos_1_tDBInput_1 + "'.");

				globalMap.put("processos_1_tDBInput_1_QUERY", dbquery_processos_1_tDBInput_1);

				java.sql.ResultSet rs_processos_1_tDBInput_1 = null;

				try {
					rs_processos_1_tDBInput_1 = stmt_processos_1_tDBInput_1
							.executeQuery(dbquery_processos_1_tDBInput_1);
					java.sql.ResultSetMetaData rsmd_processos_1_tDBInput_1 = rs_processos_1_tDBInput_1.getMetaData();
					int colQtyInRs_processos_1_tDBInput_1 = rsmd_processos_1_tDBInput_1.getColumnCount();

					String tmpContent_processos_1_tDBInput_1 = null;

					log.debug("processos_1_tDBInput_1 - Retrieving records from the database.");

					while (rs_processos_1_tDBInput_1.next()) {
						nb_line_processos_1_tDBInput_1++;

						if (colQtyInRs_processos_1_tDBInput_1 < 1) {
							processos_1_row2.id_assunto = 0;
						} else {

							processos_1_row2.id_assunto = rs_processos_1_tDBInput_1.getInt(1);
							if (rs_processos_1_tDBInput_1.wasNull()) {
								throw new RuntimeException("Null value in non-Nullable column");
							}
						}
						if (colQtyInRs_processos_1_tDBInput_1 < 2) {
							processos_1_row2.cod_assunto_cnj = null;
						} else {

							processos_1_row2.cod_assunto_cnj = routines.system.JDBCUtil
									.getString(rs_processos_1_tDBInput_1, 2, false);
						}
						if (colQtyInRs_processos_1_tDBInput_1 < 3) {
							processos_1_row2.descricao_assunto = null;
						} else {

							processos_1_row2.descricao_assunto = routines.system.JDBCUtil
									.getString(rs_processos_1_tDBInput_1, 3, false);
						}
						if (colQtyInRs_processos_1_tDBInput_1 < 4) {
							processos_1_row2.ramo_direito = null;
						} else {

							processos_1_row2.ramo_direito = routines.system.JDBCUtil
									.getString(rs_processos_1_tDBInput_1, 4, false);
						}

						log.debug("processos_1_tDBInput_1 - Retrieving the record " + nb_line_processos_1_tDBInput_1
								+ ".");

						/**
						 * [processos_1_tDBInput_1 begin ] stop
						 */

						/**
						 * [processos_1_tDBInput_1 main ] start
						 */

						s(currentComponent = "processos_1_tDBInput_1");

						// QTUP-3575
						if (enableLineage && init_processos_1_tDBInput_1_0) {
							class SchemaUtil_processos_1_row2 {

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
										final processos_1_row2Struct processos_1_row2) {
									java.util.List<java.util.Map<String, String>> s = new java.util.ArrayList<>();
									if (processos_1_row2 == null) {
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

							if (processos_1_row2 != null) {
								talendJobLog.addConnectionSchemaMessage("processos_1_tDBInput_1", "tPostgresqlInput",
										"processos_1_tFileOutputParquet_2", "tFileOutputParquet",
										"processos_1_row2" + iterateId,
										new SchemaUtil_processos_1_row2().getSchema(processos_1_row2));
								talendJobLogProcess(globalMap);
								init_processos_1_tDBInput_1_0 = false;
							}

						}
						// QTUP-3575

						tos_count_processos_1_tDBInput_1++;

						/**
						 * [processos_1_tDBInput_1 main ] stop
						 */

						/**
						 * [processos_1_tDBInput_1 process_data_begin ] start
						 */

						s(currentComponent = "processos_1_tDBInput_1");

						/**
						 * [processos_1_tDBInput_1 process_data_begin ] stop
						 */

						/**
						 * [processos_1_tFileOutputParquet_2 main ] start
						 */

						s(currentComponent = "processos_1_tFileOutputParquet_2");

						if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

								, "processos_1_row2", "processos_1_tDBInput_1", "tDBInput_1", "tPostgresqlInput",
								"processos_1_tFileOutputParquet_2", "tFileOutputParquet_2", "tFileOutputParquet"

						)) {
							talendJobLogProcess(globalMap);
						}

						if (log.isTraceEnabled()) {
							log.trace("processos_1_row2 - "
									+ (processos_1_row2 == null ? "" : processos_1_row2.toLogString()));
						}

						org.talend.parquet.data.Group group_processos_1_tFileOutputParquet_2 = factory_processos_1_tFileOutputParquet_2
								.newGroup();

						group_processos_1_tFileOutputParquet_2.append("id_assunto", processos_1_row2.id_assunto);
						if (processos_1_row2.cod_assunto_cnj != null) {

							group_processos_1_tFileOutputParquet_2.append("cod_assunto_cnj",
									String.valueOf(processos_1_row2.cod_assunto_cnj));
						}

						if (processos_1_row2.descricao_assunto != null) {

							group_processos_1_tFileOutputParquet_2.append("descricao_assunto",
									String.valueOf(processos_1_row2.descricao_assunto));
						}

						if (processos_1_row2.ramo_direito != null) {

							group_processos_1_tFileOutputParquet_2.append("ramo_direito",
									String.valueOf(processos_1_row2.ramo_direito));
						}

						writer_processos_1_tFileOutputParquet_2.write(group_processos_1_tFileOutputParquet_2);
						nb_line_processos_1_tFileOutputParquet_2++;
						log.debug("processos_1_tFileOutputParquet_2 - Writing the record "
								+ nb_line_processos_1_tFileOutputParquet_2 + " to the file.");

						tos_count_processos_1_tFileOutputParquet_2++;

						/**
						 * [processos_1_tFileOutputParquet_2 main ] stop
						 */

						/**
						 * [processos_1_tFileOutputParquet_2 process_data_begin ] start
						 */

						s(currentComponent = "processos_1_tFileOutputParquet_2");

						/**
						 * [processos_1_tFileOutputParquet_2 process_data_begin ] stop
						 */

						/**
						 * [processos_1_tFileOutputParquet_2 process_data_end ] start
						 */

						s(currentComponent = "processos_1_tFileOutputParquet_2");

						/**
						 * [processos_1_tFileOutputParquet_2 process_data_end ] stop
						 */

						/**
						 * [processos_1_tDBInput_1 process_data_end ] start
						 */

						s(currentComponent = "processos_1_tDBInput_1");

						/**
						 * [processos_1_tDBInput_1 process_data_end ] stop
						 */

						/**
						 * [processos_1_tDBInput_1 end ] start
						 */

						s(currentComponent = "processos_1_tDBInput_1");

					}
				} finally {
					if (rs_processos_1_tDBInput_1 != null) {
						rs_processos_1_tDBInput_1.close();
					}
					if (stmt_processos_1_tDBInput_1 != null) {
						stmt_processos_1_tDBInput_1.close();
					}
					if (conn_processos_1_tDBInput_1 != null && !conn_processos_1_tDBInput_1.isClosed()) {

						log.debug("processos_1_tDBInput_1 - Closing the connection to the database.");

						conn_processos_1_tDBInput_1.close();

						if ("com.mysql.cj.jdbc.Driver".equals((String) globalMap.get("driverClass_"))
								&& routines.system.BundleUtils.inOSGi()) {
							Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread")
									.getMethod("checkedShutdown").invoke(null, (Object[]) null);
						}

						log.debug("processos_1_tDBInput_1 - Connection to the database closed.");

					}

				}
				globalMap.put("processos_1_tDBInput_1_NB_LINE", nb_line_processos_1_tDBInput_1);
				log.debug("processos_1_tDBInput_1 - Retrieved records count: " + nb_line_processos_1_tDBInput_1 + " .");

				if (log.isDebugEnabled())
					log.debug("processos_1_tDBInput_1 - " + ("Done."));

				ok_Hash.put("processos_1_tDBInput_1", true);
				end_Hash.put("processos_1_tDBInput_1", System.currentTimeMillis());

				/**
				 * [processos_1_tDBInput_1 end ] stop
				 */

				/**
				 * [processos_1_tFileOutputParquet_2 end ] start
				 */

				s(currentComponent = "processos_1_tFileOutputParquet_2");

				globalMap.put("processos_1_tFileOutputParquet_2_NB_LINE", nb_line_processos_1_tFileOutputParquet_2);

				log.debug("processos_1_tFileOutputParquet_2 - Written records count: "
						+ nb_line_processos_1_tFileOutputParquet_2 + " .");

				if (writer_processos_1_tFileOutputParquet_2 != null) {
					writer_processos_1_tFileOutputParquet_2.close();
				}
				org.apache.hadoop.fs.FileSystem fs_processos_1_tFileOutputParquet_2 = crcPath_processos_1_tFileOutputParquet_2
						.getFileSystem(config_processos_1_tFileOutputParquet_2);
				if (fs_processos_1_tFileOutputParquet_2.exists(crcPath_processos_1_tFileOutputParquet_2)) {
					fs_processos_1_tFileOutputParquet_2.delete(crcPath_processos_1_tFileOutputParquet_2, false);
				}

				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, "processos_1_row2", 2, 0,
						"processos_1_tDBInput_1", "tDBInput_1", "tPostgresqlInput", "processos_1_tFileOutputParquet_2",
						"tFileOutputParquet_2", "tFileOutputParquet", "output")) {
					talendJobLogProcess(globalMap);
				}

				if (log.isDebugEnabled())
					log.debug("processos_1_tFileOutputParquet_2 - " + ("Done."));

				ok_Hash.put("processos_1_tFileOutputParquet_2", true);
				end_Hash.put("processos_1_tFileOutputParquet_2", System.currentTimeMillis());

				if (execStat) {
					runStat.updateStatOnConnection("processos_1_OnComponentOk3", 0, "ok");
				}
				processos_1_tS3Put_1Process(globalMap);

				/**
				 * [processos_1_tFileOutputParquet_2 end ] stop
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
				 * [processos_1_tDBInput_1 finally ] start
				 */

				s(currentComponent = "processos_1_tDBInput_1");

				/**
				 * [processos_1_tDBInput_1 finally ] stop
				 */

				/**
				 * [processos_1_tFileOutputParquet_2 finally ] start
				 */

				s(currentComponent = "processos_1_tFileOutputParquet_2");

				/**
				 * [processos_1_tFileOutputParquet_2 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("processos_1_tDBInput_1_SUBPROCESS_STATE", 1);
	}

	public void processos_1_tS3Put_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("processos_1_tS3Put_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("processos_1_tS3Put_1", "VQEQZ6_");

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
				 * [processos_1_tS3Put_1 begin ] start
				 */

				sh("processos_1_tS3Put_1");

				s(currentComponent = "processos_1_tS3Put_1");

				int tos_count_processos_1_tS3Put_1 = 0;

				if (log.isDebugEnabled())
					log.debug("processos_1_tS3Put_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_processos_1_tS3Put_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_processos_1_tS3Put_1 = new StringBuilder();
							log4jParamters_processos_1_tS3Put_1.append("Parameters:");
							log4jParamters_processos_1_tS3Put_1
									.append("configuration.bucket" + " = " + "peta-demo-qlik");
							log4jParamters_processos_1_tS3Put_1.append(" | ");
							log4jParamters_processos_1_tS3Put_1.append("configuration.key" + " = "
									+ "\"silver/\" + ((String)globalMap.get(\"tFileList_1_CURRENT_FILE\"))");
							log4jParamters_processos_1_tS3Put_1.append(" | ");
							log4jParamters_processos_1_tS3Put_1.append("configuration.file" + " = "
									+ "\"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/processos_silver.parquet\"");
							log4jParamters_processos_1_tS3Put_1.append(" | ");
							log4jParamters_processos_1_tS3Put_1
									.append("configuration.enableServerSideEncryption" + " = " + "false");
							log4jParamters_processos_1_tS3Put_1.append(" | ");
							log4jParamters_processos_1_tS3Put_1.append("configuration.dieOnError" + " = " + "false");
							log4jParamters_processos_1_tS3Put_1.append(" | ");
							log4jParamters_processos_1_tS3Put_1
									.append("configuration.multipartThreshold" + " = " + "5");
							log4jParamters_processos_1_tS3Put_1.append(" | ");
							log4jParamters_processos_1_tS3Put_1.append("configuration.partSize" + " = " + "5");
							log4jParamters_processos_1_tS3Put_1.append(" | ");
							log4jParamters_processos_1_tS3Put_1
									.append("configuration.cannedAccessControlList" + " = " + "NONE");
							log4jParamters_processos_1_tS3Put_1.append(" | ");
							log4jParamters_processos_1_tS3Put_1
									.append("configuration.enableObjectLock" + " = " + "false");
							log4jParamters_processos_1_tS3Put_1.append(" | ");
							log4jParamters_processos_1_tS3Put_1.append("configuration.setObjectTags" + " = " + "false");
							log4jParamters_processos_1_tS3Put_1.append(" | ");
							log4jParamters_processos_1_tS3Put_1.append("USE_EXISTING_CONNECTION" + " = " + "true");
							log4jParamters_processos_1_tS3Put_1.append(" | ");
							log4jParamters_processos_1_tS3Put_1
									.append("CONNECTION" + " = " + "processos_1_tS3Connection_1");
							log4jParamters_processos_1_tS3Put_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("processos_1_tS3Put_1 - " + (log4jParamters_processos_1_tS3Put_1));
						}
					}
					new BytesLimit65535_processos_1_tS3Put_1().limitLog4jByte();
				}
				// QTUP-3575
				if (enableLineage) {
					class ParameterUtil_processos_1_tS3Put_1 {

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

								component_parameters.put("configuration.key", String
										.valueOf("silver/" + ((String) globalMap.get("tFileList_1_CURRENT_FILE"))));

								component_parameters.put("configuration.file",
										"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/processos_silver.parquet");

								component_parameters.put("configuration.enableServerSideEncryption", "false");

								component_parameters.put("configuration.dieOnError", "false");

								component_parameters.put("configuration.multipartThreshold", "5");

								component_parameters.put("configuration.partSize", "5");

								component_parameters.put("configuration.cannedAccessControlList", "NONE");

								component_parameters.put("configuration.enableObjectLock", "false");

								component_parameters.put("configuration.setObjectTags", "false");
								component_parameters.put("USE_EXISTING_CONNECTION", String.valueOf("true"));
								component_parameters.put("CONNECTION", String.valueOf("processos_1_tS3Connection_1"));

							} catch (java.lang.Exception e_processos_1_tS3Put_1) {
								// do nothing
							}

							return component_parameters;
						}
					}

					talendJobLog.addComponentParameterMessage("processos_1_tS3Put_1", "S3Put",
							new ParameterUtil_processos_1_tS3Put_1().getParameter());
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}
				// QTUP-3575

				if (enableLogStash) {
					talendJobLog.addCM("processos_1_tS3Put_1", "tS3Put_1", "S3Put");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				final org.talend.sdk.component.runtime.manager.ComponentManager mgr_processos_1_tS3Put_1 = org.talend.sdk.component.runtime.manager.ComponentManager
						.instance();
				mgr_processos_1_tS3Put_1.autoDiscoverPluginsIfEmpty(false, true);

				final java.util.Map<String, String> configuration_processos_1_tS3Put_1 = new java.util.HashMap<>();
				final java.util.Map<String, String> registry_metadata_processos_1_tS3Put_1 = new java.util.HashMap<>();

				final class SettingHelper_processos_1_tS3Put_1 {
					final java.util.Map<String, String> configuration;

					SettingHelper_processos_1_tS3Put_1(final java.util.Map<String, String> configuration) {
						this.configuration = configuration;
					}

					void put(String key, String value) {
						if (value != null) {
							configuration.put(key, value);
						}
					}
				}

				final SettingHelper_processos_1_tS3Put_1 s_processos_1_tS3Put_1 = new SettingHelper_processos_1_tS3Put_1(
						configuration_processos_1_tS3Put_1);
				Object dv_processos_1_tS3Put_1;
				java.net.URL mappings_url_processos_1_tS3Put_1 = this.getClass().getResource("/xmlMappings");
				globalMap.put("processos_1_tS3Put_1_MAPPINGS_URL", mappings_url_processos_1_tS3Put_1);
				globalMap.putIfAbsent("TALEND_PRODUCT_VERSION", "8.0");
				globalMap.putIfAbsent("TALEND_AWS_TRACKER", "APN/1.0 Talend/8.0 Studio/8.0 (Qlik Talend Cloud)");

				s_processos_1_tS3Put_1.put("configuration.bucket", "peta-demo-qlik");

				dv_processos_1_tS3Put_1 = "silver/" + ((String) globalMap.get("tFileList_1_CURRENT_FILE"));
				if (dv_processos_1_tS3Put_1 instanceof java.io.InputStream) {
					s_processos_1_tS3Put_1.put("configuration.key",
							"\"silver/\" + ((String)globalMap.get(\"tFileList_1_CURRENT_FILE\"))");
				} else {
					s_processos_1_tS3Put_1.put("configuration.key",
							String.valueOf("silver/" + ((String) globalMap.get("tFileList_1_CURRENT_FILE"))));
				}

				s_processos_1_tS3Put_1.put("configuration.file",
						"C:/Users/arauj/Documents/NEXUDATA/08. PetaCorp/POC's/Dados/TRF5/Data/processos_silver.parquet");

				s_processos_1_tS3Put_1.put("configuration.enableServerSideEncryption", "false");

				s_processos_1_tS3Put_1.put("configuration.dieOnError", "false");

				s_processos_1_tS3Put_1.put("configuration.multipartThreshold", "5");

				s_processos_1_tS3Put_1.put("configuration.partSize", "5");

				s_processos_1_tS3Put_1.put("configuration.cannedAccessControlList", "NONE");

				s_processos_1_tS3Put_1.put("configuration.enableObjectLock", "false");

				s_processos_1_tS3Put_1.put("configuration.setObjectTags", "false");

				s_processos_1_tS3Put_1.put("configuration.dataset.__version", "-1");

				s_processos_1_tS3Put_1.put("configuration.dataset.datastore.__version", "-1");
				final class SchemaSettingHelper_processos_1_tS3Put_1_1 {

					public void set(final java.util.Map<String, String> configuration) throws java.lang.Exception {
					}
				}
				new SchemaSettingHelper_processos_1_tS3Put_1_1().set(configuration_processos_1_tS3Put_1);
				final class SchemaSettingHelper_processos_1_tS3Put_1_2 {

					public void set(final java.util.Map<String, String> configuration) throws java.lang.Exception {
					}
				}
				new SchemaSettingHelper_processos_1_tS3Put_1_2().set(configuration_processos_1_tS3Put_1);
				final java.util.Map<String, String> config_from_connection_processos_1_tS3Put_1 = (java.util.Map<String, String>) globalMap
						.get("configuration_processos_1_tS3Connection_1");
				final String conn_param_prefix_processos_1_tS3Put_1 = "configuration.dataset.datastore";
				if (config_from_connection_processos_1_tS3Put_1 != null
						&& conn_param_prefix_processos_1_tS3Put_1 != null) {
					final String prefix_processos_1_tS3Put_1 = config_from_connection_processos_1_tS3Put_1.keySet()
							.stream()
							.filter(key_processos_1_tS3Put_1 -> key_processos_1_tS3Put_1.endsWith(".__version"))
							.findFirst().map(key_processos_1_tS3Put_1 -> key_processos_1_tS3Put_1.substring(0,
									key_processos_1_tS3Put_1.lastIndexOf(".__version")))
							.orElse(null);

					if (prefix_processos_1_tS3Put_1 != null) {
						config_from_connection_processos_1_tS3Put_1.entrySet().stream()
								.filter(entry_processos_1_tS3Put_1 -> entry_processos_1_tS3Put_1.getKey()
										.startsWith(prefix_processos_1_tS3Put_1))
								.forEach(entry_processos_1_tS3Put_1 -> {
									configuration_processos_1_tS3Put_1.put(
											entry_processos_1_tS3Put_1.getKey().replaceFirst(
													prefix_processos_1_tS3Put_1,
													conn_param_prefix_processos_1_tS3Put_1),
											entry_processos_1_tS3Put_1.getValue());
								});
					}
				}

				final org.talend.sdk.component.runtime.standalone.DriverRunner standalone_processos_1_tS3Put_1 = mgr_processos_1_tS3Put_1
						.findDriverRunner("S3", "Put", 1, configuration_processos_1_tS3Put_1)
						.orElseThrow(() -> new IllegalArgumentException("Can't find S3#Put"));

				org.talend.sdk.component.runtime.di.studio.RuntimeContextInjector.injectLifecycle(
						standalone_processos_1_tS3Put_1, new org.talend.sdk.component.api.context.RuntimeContextHolder(
								"processos_1_tS3Put_1", globalMap));

				try {
					java.lang.reflect.Field field_processos_1_tS3Put_1 = standalone_processos_1_tS3Put_1.getClass()
							.getSuperclass().getDeclaredField("delegate");
					if (!field_processos_1_tS3Put_1.isAccessible()) {
						field_processos_1_tS3Put_1.setAccessible(true);
					}
					Object v_processos_1_tS3Put_1 = field_processos_1_tS3Put_1.get(standalone_processos_1_tS3Put_1);
					Object con_processos_1_tS3Put_1 = globalMap.get("conn_processos_1_tS3Connection_1");
					if (con_processos_1_tS3Put_1 == null) {
						throw new RuntimeException("can't find the connection object");
					}

					Class<?> current_processos_1_tS3Put_1 = v_processos_1_tS3Put_1.getClass();
					while (current_processos_1_tS3Put_1 != null && current_processos_1_tS3Put_1 != Object.class) {
						java.util.stream.Stream.of(current_processos_1_tS3Put_1.getDeclaredFields())
								.filter(f_processos_1_tS3Put_1 -> f_processos_1_tS3Put_1.isAnnotationPresent(
										org.talend.sdk.component.api.service.connection.Connection.class))
								.forEach(f_processos_1_tS3Put_1 -> {
									if (!f_processos_1_tS3Put_1.isAccessible()) {
										f_processos_1_tS3Put_1.setAccessible(true);
									}
									try {
										f_processos_1_tS3Put_1.set(v_processos_1_tS3Put_1, con_processos_1_tS3Put_1);
									} catch (final IllegalAccessException e_processos_1_tS3Put_1) {
										throw new IllegalStateException(e_processos_1_tS3Put_1);
									}
								});
						current_processos_1_tS3Put_1 = current_processos_1_tS3Put_1.getSuperclass();
					}
				} catch (Exception e_processos_1_tS3Put_1) {
					throw e_processos_1_tS3Put_1;
				}

				standalone_processos_1_tS3Put_1.start();
				globalMap.put("standalone_processos_1_tS3Put_1", standalone_processos_1_tS3Put_1);

				standalone_processos_1_tS3Put_1.runAtDriver();
//Standalone begin stub

				/**
				 * [processos_1_tS3Put_1 begin ] stop
				 */

				/**
				 * [processos_1_tS3Put_1 main ] start
				 */

				s(currentComponent = "processos_1_tS3Put_1");

				tos_count_processos_1_tS3Put_1++;

				/**
				 * [processos_1_tS3Put_1 main ] stop
				 */

				/**
				 * [processos_1_tS3Put_1 process_data_begin ] start
				 */

				s(currentComponent = "processos_1_tS3Put_1");

				/**
				 * [processos_1_tS3Put_1 process_data_begin ] stop
				 */

				/**
				 * [processos_1_tS3Put_1 process_data_end ] start
				 */

				s(currentComponent = "processos_1_tS3Put_1");

				/**
				 * [processos_1_tS3Put_1 process_data_end ] stop
				 */

				/**
				 * [processos_1_tS3Put_1 end ] start
				 */

				s(currentComponent = "processos_1_tS3Put_1");

				if (standalone_processos_1_tS3Put_1 != null) {
					standalone_processos_1_tS3Put_1.stop();
				}

				globalMap.remove("standalone_processos_1_tS3Put_1");

				if (log.isDebugEnabled())
					log.debug("processos_1_tS3Put_1 - " + ("Done."));

				ok_Hash.put("processos_1_tS3Put_1", true);
				end_Hash.put("processos_1_tS3Put_1", System.currentTimeMillis());

				/**
				 * [processos_1_tS3Put_1 end ] stop
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
				 * [processos_1_tS3Put_1 finally ] start
				 */

				s(currentComponent = "processos_1_tS3Put_1");

				final org.talend.sdk.component.runtime.standalone.DriverRunner standalone_processos_1_tS3Put_1 = org.talend.sdk.component.runtime.standalone.DriverRunner.class
						.cast(globalMap.remove("standalone_processos_1_tS3Put_1"));
				try {
					if (standalone_processos_1_tS3Put_1 != null) {
						standalone_processos_1_tS3Put_1.stop();
					}
				} catch (final RuntimeException re) {
					throw new TalendException(re, currentComponent, cLabel, globalMap);
				}

				/**
				 * [processos_1_tS3Put_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("processos_1_tS3Put_1_SUBPROCESS_STATE", 1);
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
		org.slf4j.MDC.put("_compiledAtTimestamp", "2026-03-18T00:21:47.420522900Z");

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
			processos_1_tS3Connection_1Process(globalMap);
			if (!"failure".equals(status)) {
				status = "end";
			}
		} catch (TalendException e_processos_1_tS3Connection_1) {
			globalMap.put("processos_1_tS3Connection_1_SUBPROCESS_STATE", -1);

			e_processos_1_tS3Connection_1.printStackTrace();

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

		connections.put("conn_processos_1_tS3Connection_1", globalMap.get("conn_processos_1_tS3Connection_1"));

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
 * 270224 characters generated by Qlik Talend Cloud Enterprise Edition on the 17
 * de março de 2026 21:21:47 BRT
 ************************************************************************************************/